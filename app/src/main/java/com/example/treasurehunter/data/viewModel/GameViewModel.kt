package com.example.treasurehunter.data.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.treasurehunter.data.model.ScreenMode
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

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

        // Coordinates

        private var _randomLocations = mutableStateOf<List<LatLng>>(emptyList())
        val randomLocations: List<LatLng> get() = _randomLocations.value

        fun generateRandomLocations(center: LatLng, radius: Double, count: Int = 9) {
            val randomList = mutableListOf<LatLng>()
            for (i in 1..count) {
                val randomPoint = generateRandomLocation(center, radius)
                randomList.add(randomPoint)
            }
            _randomLocations.value = randomList

            // Log các tọa độ đã tạo
            randomList.forEachIndexed { index, latLng ->
                Log.d("RandomLocation", "Location $index: (${latLng.latitude}, ${latLng.longitude})")
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
    }
}