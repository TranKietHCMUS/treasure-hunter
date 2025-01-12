import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.maps.android.compose.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPositionState = rememberCameraPositionState()

    // Launcher để xin quyền
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Khởi tạo FusedLocationProviderClient
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Lấy vị trí hiện tại khi có quyền
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                // Sử dụng getCurrentLocation thay vì lastLocation để lấy vị trí chính xác hơn
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(listener: OnTokenCanceledListener) = CancellationTokenSource().token
                        override fun isCancellationRequested() = false
                    }
                ).addOnSuccessListener { location: Location? ->
                    location?.let {
                        val position = LatLng(it.latitude, it.longitude)
                        currentLocation = position
                        // Di chuyển camera đến vị trí hiện tại
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(position, 15f)
                    }
                }
            } catch (e: SecurityException) {
                hasLocationPermission = false
            }
        } else {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        if (hasLocationPermission) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = true
                ),
                cameraPositionState = cameraPositionState
            ) {
                // Vẽ vùng tròn khi đã có vị trí
                currentLocation?.let { location ->
                    Circle(
                        center = location,
                        clickable = true,
                        fillColor = Color(0x4D808080),
                        strokeColor = Color(0xFFFF0000),
                        strokeWidth = 2f,
                        radius = 1000.0  // 1km = 1000m
                    )
                }
            }
        }else {
            Text("Ứng dụng cần quyền truy cập vị trí để hoạt động")
        }
    }
}