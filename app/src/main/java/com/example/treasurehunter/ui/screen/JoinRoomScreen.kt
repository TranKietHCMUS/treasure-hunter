package com.example.treasurehunter.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.treasurehunter.data.viewModel.RoomViewModel
import com.example.treasurehunter.data.viewModel.SocketViewModel

@Composable
fun JoinRoomScreen() {
    val viewModel = SocketViewModel.room

    val roomId by viewModel.roomId
    val members by viewModel.members
    val joinedRoom by viewModel.joinedRoom
    val message by viewModel.message

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
        Text(text = "Room ID: $roomId")
        Text(text = "Joined Room: $joinedRoom")
        Text(text = "Message: $message")
        Text(text = "members: $members")
    }
}