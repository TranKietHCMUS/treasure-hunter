import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.treasurehunter.data.viewModel.GameViewModel

@Composable
fun MapScreen(
    modifier: Modifier = Modifier
) {
    val gameLocation = GameViewModel.gameLocation
    val gameRadius = GameViewModel.gameRadius
    val cameraPositionState = rememberCameraPositionState()
//    val randomLocations = GameViewModel.randomLocations

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
                            title = "Kho báu $index",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                            onClick = {
                                // Đánh dấu kho báu này là "đã tìm thấy"
                                GameViewModel.markTreasureAsFound(treasure.location)
                                true // Xử lý sự kiện nhấn marker
                            }
                        )
                    }
                }
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