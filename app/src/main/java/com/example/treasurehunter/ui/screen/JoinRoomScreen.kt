package com.example.treasurehunter.ui.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treasurehunter.BuildConfig
import com.example.treasurehunter.LocalNavController
import com.example.treasurehunter.data.viewModel.PuzzleViewModel
import com.example.treasurehunter.data.viewModel.SocketViewModel
import com.example.treasurehunter.ui.component.BackButton


@Composable
fun JoinRoomScreen() {
    val navController = LocalNavController.current
    val viewModel = SocketViewModel.room

    val joinedRoom by viewModel.joinedRoom
    val message by viewModel.message

    LaunchedEffect(Unit) {
        Log.i("SOCKET", "RoomScreen: LaunchedEffect")
        viewModel.connectToServer(BuildConfig.IP, BuildConfig.PORT)
    }

    LaunchedEffect(message) {
        Log.i("SOCKET", "RoomScreen: LaunchedEffect, message: $message")
        if (message.startsWith("Game started!")) {
            viewModel.inGame()
            navController.navigate("setting-room")
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

            Button(
                onClick = { viewModel.joinRoom(inputRoomCode) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D2E),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Join Room",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            if (joinedRoom != "") {
                val roomId = joinedRoom.split(":")[1]
                SocketViewModel.room.roomId.value = roomId
                Text(text = "Joined room: $roomId successfully")
            }

        }
    }

}