import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.R

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


fun resizeIcon(resourceId: Int, width: Int, height: Int, context: Context): BitmapDescriptor {
    val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
    return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
}

@Composable
fun MapScreen(
    modifier: Modifier = Modifier
) {

    val gameLocation = GameViewModel.gameLocation
    val gameRadius = GameViewModel.gameRadius
    val currentUserLocation = GameViewModel.currentUserLocation
    val cameraPositionState = rememberCameraPositionState()

    val context = LocalContext.current
    val gameViewModel: GameViewModel = viewModel()

    // Khởi tạo GameViewModel
    LaunchedEffect(context) {
        GameViewModel.initialize(context)
    }

    // Gọi fetchCurrentLocation
    LaunchedEffect(context) {
        GameViewModel.fetchCurrentLocation(
            context = context,
            onSuccess = { location ->
                Log.d("MapScreen", "User location fetched: $location")
            },
            onFailure = {
                Log.d("MapScreen", "Failed to fetch user location")
            }
        )
    }

    // Gọi startTrackingUserLocation để cập nhật vị trí liên tục
    LaunchedEffect(context) {
        GameViewModel.startTrackingUserLocation(context) { location ->
//            Log.d("MapScreen", "Updated user location: $location")
        }
    }


    // Di chuyển camera đến vị trí game khi có vị trí
    LaunchedEffect(gameLocation) {
        gameLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        if (gameLocation != null) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = MapProperties(
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false // Tắt nút vị trí vì không cần thiết
                ),
                cameraPositionState = cameraPositionState
            ) {
                // Vẽ vòng tròn với bán kính từ GameViewModel
                Circle(
                    center = gameLocation,
                    radius = gameRadius, // Sử dụng bán kính từ ViewModel
                    fillColor = Color(0x4D808080),
                    strokeColor = Color(0xFFFF6D2E),
                    strokeWidth = 2f,
                )

                // Hiển thị các tọa độ ngẫu nhiên là các chấm đỏ
                GameViewModel.treasures.forEachIndexed { index, treasure ->
                    if (!treasure.found) {
                        Marker(
                            state = MarkerState(position = treasure.location),
                            title = "Treasure $index",
                            icon = remember {
                                resizeIcon(R.drawable.treasure_icon, 80, 80, context) // Thay đổi kích thước 80x80 pixel
                            },
                            // Replace with your chest icon
                            onClick = {
                                // Mark treasure as "found"
                                GameViewModel.markTreasureAsFound(treasure.location)
                                true // Handle marker click event
                            }

                        )
                    }
                }

                currentUserLocation?.let {
                    Log.d("MapScreen", "Current user location: $it")
                    Marker(
                        state = MarkerState(position = it),
                        title = "Your Location",
                        icon = remember {
                            resizeIcon(R.drawable.user_location, 100, 100, context) // Thay đổi kích thước icon vị trí người dùng
                        }
                    )
                }

//                Log.d("MapScreen", "Current user location: $currentUserLocation")

            }
        } else {
            // Hiển thị thông báo nếu chưa có vị trí
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Vui lòng tạo phòng chơi trước",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}