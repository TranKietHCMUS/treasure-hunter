package com.example.treasurehunter.ui.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.treasurehunter.LocalNavController
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.data.viewModel.PuzzleViewModel
import com.example.treasurehunter.ui.component.BackButton

@Composable
fun RoomControlScreen() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Yêu cầu quyền vị trí
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (hasLocationPermission) {
            GameViewModel.initialize(context)
            GameViewModel.fetchCurrentLocation(context, {}, {})
        } else {
            showPermissionDialog = true // Nếu từ chối quyền, hiển thị thông báo
        }
    }

    // Kiểm tra quyền khi vào màn hình
    LaunchedEffect(Unit) {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        hasLocationPermission = permissionGranted

        if (!permissionGranted) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            GameViewModel.initialize(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5881F), Color(0xFFF5881F), Color.White)
                )
            )
    ) {
        BackButton(onBackPress = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (hasLocationPermission) {
                        PuzzleViewModel.init()
                        navController.navigate("setting-room")
                    } else {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = "Create room",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (hasLocationPermission) {
                        PuzzleViewModel.init()
                        navController.navigate("join-room")
                    } else {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = "Join room",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Hiển thị thông báo khi quyền bị từ chối
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Quyền vị trí bị từ chối") },
            text = { Text("Trò chơi cần quyền vị trí để hoạt động. Vui lòng cấp quyền trong Cài đặt.") },
            confirmButton = {
                Button(onClick = {
                    showPermissionDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Mở Cài đặt")
                }
            },
            dismissButton = {
                Button(onClick = { showPermissionDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}
