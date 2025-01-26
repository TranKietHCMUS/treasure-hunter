package com.example.treasurehunter.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
import com.example.treasurehunter.data.viewModel.SocketViewModel
import kotlinx.coroutines.delay

@Preview
@Composable
fun CreateRoomScreen() {
    val navController = LocalNavController.current
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
        // Tạo phòng
        Button(onClick = { viewModel.createRoom() }) {
            Text("Create Room")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (roomId != "") {
            Text( modifier = Modifier
                .padding(16.dp)
                .background(Color.White),
                fontSize = 20.sp,
                text = "Room ID: $roomId")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.startGame(roomId)
                viewModel.inGame()
                navController.navigate("setting-room")
            } ) {
                Text("Start Game")
            }
        }
    }
}