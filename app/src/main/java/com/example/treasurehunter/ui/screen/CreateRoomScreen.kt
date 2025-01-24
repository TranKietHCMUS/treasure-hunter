package com.example.treasurehunter.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.treasurehunter.LocalNavController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.treasurehunter.data.viewModel.SocketViewModel
import kotlinx.coroutines.delay

@Preview
@Composable
fun CreateRoomScreen() {
    val viewModel = SocketViewModel.room

    val roomId by viewModel.roomId
    val members by viewModel.members

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5881F), Color(0xFFFFA726), Color.White)
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF5881F), Color(0xFFFFA726), Color.White)
                    )
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Tạo phòng
            Button(onClick = { viewModel.createRoom() }) {
                Text("Create Room")
            }
            Text(text = "Room ID: $roomId")
            Text(text = "Members: $members")

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}