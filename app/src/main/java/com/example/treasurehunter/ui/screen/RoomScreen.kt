package com.example.treasurehunter.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.treasurehunter.data.viewModel.RoomViewModel

@Composable
fun RoomScreen(viewModel: RoomViewModel = viewModel()) {
    val roomId by viewModel.roomId
    val message by viewModel.message
    val joinedRoom by viewModel.joinedRoom

    LaunchedEffect(Unit) {
        Log.i("SOCKET", "RoomScreen: LaunchedEffect")
        viewModel.connectToServer("192.168.1.8", 8080)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Message: $message")

        Spacer(modifier = Modifier.height(16.dp))

        // Tạo phòng
        Button(onClick = { viewModel.createRoom() }) {
            Text("Create Room")
        }
        Text(text = "Room ID: $roomId")

        Spacer(modifier = Modifier.height(16.dp))

        // Tham gia phòng
        var inputRoomCode by remember { mutableStateOf("") }
        TextField(
            value = inputRoomCode,
            onValueChange = { inputRoomCode = it },
            label = { Text("Enter Room Code") }
        )
        Button(onClick = { viewModel.joinRoom(inputRoomCode) }) {
            Text("Join Room")
        }
        Text(text = "Joined Room: $joinedRoom")
    }
}
