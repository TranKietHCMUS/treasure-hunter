package com.example.treasurehunter.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.app.auth.AuthViewModel
import com.example.treasurehunter.BuildConfig
import com.example.treasurehunter.LocalNavController
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.data.viewModel.PuzzleViewModel
import com.example.treasurehunter.data.viewModel.SocketViewModel
import com.example.treasurehunter.ui.component.BackButton
import com.example.treasurehunter.ui.component.Loading
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener


@Composable
fun JoinRoomScreen() {
    val navController = LocalNavController.current
    val viewModel = SocketViewModel.room

    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val currentUser by AuthViewModel.currentUser.collectAsState(initial = null)

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var isLoading by remember { mutableStateOf(false) }

    val joinedRoom by viewModel.joinedRoom
    val message by viewModel.message

    LaunchedEffect(Unit) {
        Log.i("SOCKET", "RoomScreen: LaunchedEffect")
        viewModel.connectToServer(BuildConfig.IP, BuildConfig.PORT)
    }

    LaunchedEffect(message) {
        Log.i("SOCKET", "RoomScreen: LaunchedEffect, message: $message")
        if (message.startsWith("Game started!")) {
            if (hasLocationPermission) {
                isLoading = true
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(listener: OnTokenCanceledListener) = CancellationTokenSource().token
                        override fun isCancellationRequested() = false
                    }
                ).addOnSuccessListener { location ->
                    location?.let {
                        val position = LatLng(it.latitude, it.longitude)
                        currentLocation = position

                        // Set game location and radius
                        GameViewModel.setGameLocation(currentLocation!!)

                        // Generate random locations
                        GameViewModel.generateTreasures(currentLocation!!, GameViewModel.gameRadius)

                        while (true) {
                            if (GameViewModel.gameLocation != null) {
                                viewModel.inGame()
                                isLoading = false
                                navController.navigate("in-game")
                                break
                            }
                        }
                    }
                }
            } else {
                // Điều hướng về RoomControlScreen nếu quyền chưa được cấp
                navController.navigate("room-control")
            }
        }
    }

    LaunchedEffect(joinedRoom) {
        if (joinedRoom.startsWith("JOIN_SUCCESS")) {
            viewModel.waitingGame()
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
            // Tham gia phòng
            var inputRoomCode by remember { mutableStateOf("") }
            TextField(
                value = inputRoomCode,
                onValueChange = { inputRoomCode = it },
                label = { Text("Enter Room Code") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CreateButton(
                isLoading = isLoading,
                enabled = true,
                onClick = { viewModel.joinRoom(currentUser?.fullName ?: "Client player", inputRoomCode) },
                myText = "Join Room"
                )

            if (joinedRoom != "") {
                val roomId = joinedRoom.split(":")[1]
                SocketViewModel.room.roomId.value = roomId
                Text(text = "Joined room: $roomId successfully")
            }

        }
        if (isLoading) {
            Loading()
        }
    }
}