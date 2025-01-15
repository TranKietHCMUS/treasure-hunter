package com.example.treasurehunter.data.viewModel

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.treasurehunter.data.model.ScreenMode
import com.example.treasurehunter.data.model.Treasure
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.ui.component.BackButton
import com.example.treasurehunter.ui.component.Loading
import com.example.treasurehunter.ui.component.Logo
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class GameViewModel @Inject constructor() : ViewModel()  {
    companion object {
        var screenMode by mutableStateOf(ScreenMode.MAP)

        fun changeScreenMode(mode: ScreenMode) {
            screenMode = mode
        }

        private var _gameLocation = mutableStateOf<LatLng?>(null)
        val gameLocation: LatLng? get() = _gameLocation.value

        private var _gameRadius = mutableStateOf(500.0)
        val gameRadius: Double get() = _gameRadius.value

        fun setGameLocation(location: LatLng) {
            _gameLocation.value = location
        }

        fun setGameRadius(radius: Double) {
            _gameRadius.value = radius
        }

        // ------------------- Treasures -------------------------
        private var _treasures = mutableStateOf<List<Treasure>>(emptyList())
        val treasures: List<Treasure> get() = _treasures.value

        // Tạo danh sách các kho báu từ tọa độ ngẫu nhiên
        fun generateTreasures(center: LatLng, radius: Double, count: Int = 9) {
            val treasureList = mutableListOf<Treasure>()
            for (i in 1..count) {
                val randomPoint = generateRandomLocation(center, radius)
                treasureList.add(Treasure(location = randomPoint))
            }
            _treasures.value = treasureList

            // Log tọa độ kho báu đã tạo
            treasureList.forEachIndexed { index, treasure ->
                Log.d("Treasure", "Treasure $index: (${treasure.location.latitude}, ${treasure.location.longitude})")
            }
        }

        // Đánh dấu kho báu đã tìm thấy
        fun markTreasureAsFound(location: LatLng) {
            _treasures.value = _treasures.value.map {
                if (it.location == location) it.copy(found = true) else it
            }
        }

        private fun generateRandomLocation(center: LatLng, radius: Double): LatLng {
            val radiusInDegrees = radius / 111000.0 // 111km ~ 1 độ
            val u = Math.random()
            val v = Math.random()
            val w = radiusInDegrees * Math.sqrt(u)
            val t = 2 * Math.PI * v
            val x = w * Math.cos(t)
            val y = w * Math.sin(t)

            val newLat = center.latitude + y
            val newLng = center.longitude + x / Math.cos(Math.toRadians(center.latitude))
            return LatLng(newLat, newLng)
        }


        // ------------------ user location ------------------

        private var _currentUserLocation = mutableStateOf<LatLng?>(null)
        val currentUserLocation: LatLng? get() = _currentUserLocation.value

        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        // Khởi tạo FusedLocationProviderClient
        fun initialize(context: Context) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        }

        // Kiểm tra quyền truy cập vị trí
        fun checkLocationPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        // Yêu cầu quyền truy cập vị trí
        fun requestLocationPermission(context: Context) {
            if (!checkLocationPermission(context)) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }

        // Lấy vị trí người dùng
        fun fetchCurrentLocation(
            context: Context,
            onSuccess: (LatLng) -> Unit,
            onFailure: () -> Unit
        ) {
            if (!checkLocationPermission(context)) {
                requestLocationPermission(context)
                onFailure()
                return
            }
            if (!::fusedLocationProviderClient.isInitialized) {
                initialize(context)
            }

            try {
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(listener: OnTokenCanceledListener) =
                            CancellationTokenSource().token
                        override fun isCancellationRequested() = false
                    }
                ).addOnSuccessListener { location ->
                    location?.let {
                        val userLocation = LatLng(it.latitude, it.longitude)
                        _currentUserLocation.value = userLocation
                        onSuccess(userLocation)
                    } ?: run {
                        onFailure()
                    }
                }.addOnFailureListener {
                    onFailure()
                }
            } catch (e: SecurityException) {
                onFailure()
            }
        }

        // Bắt đầu theo dõi vị trí người dùng
        fun startTrackingUserLocation(context: Context, onLocationChanged: (LatLng) -> Unit) {
            if (!checkLocationPermission(context)) {
                requestLocationPermission(context)
                return
            }

            // Sử dụng LocationRequest.Builder thay cho LocationRequest.create()
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setIntervalMillis(1000)  // Cập nhật mỗi 1 giây
                .setMinUpdateDistanceMeters(0f) // Cập nhật nếu người dùng di chuyển ít nhất 10m
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.firstOrNull()?.let { location ->
                        val userLocation = LatLng(location.latitude, location.longitude)
                        _currentUserLocation.value = userLocation

                        // Log vị trí người dùng mỗi khi thay đổi
                        Log.d("GameViewModel", "Updated user location: $userLocation")

                        onLocationChanged(userLocation)
                    }
                }
            }

            // Bắt đầu theo dõi vị trí
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null // Chạy trên main thread mặc định
            )
        }

        // Dừng theo dõi vị trí người dùng
        fun stopTrackingUserLocation() {
            if (::fusedLocationProviderClient.isInitialized) {
                fusedLocationProviderClient.removeLocationUpdates(object : LocationCallback() {})
            }
        }
    }
}