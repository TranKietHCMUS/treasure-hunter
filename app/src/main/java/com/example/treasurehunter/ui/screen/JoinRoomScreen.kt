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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ActivityScenario.launch
import com.example.treasurehunter.LocalNavController
import com.example.treasurehunter.data.viewModel.RoomViewModel
import com.example.treasurehunter.data.viewModel.SocketViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun JoinRoomScreen() {
    val navController = LocalNavController.current
    val viewModel = SocketViewModel.room

    val joinedRoom by viewModel.joinedRoom
    val message by viewModel.message

    LaunchedEffect(Unit) {
        Log.i("SOCKET", "RoomScreen: LaunchedEffect")
        viewModel.connectToServer("192.168.1.8", 8080)
    }

    LaunchedEffect(message) {
        Log.i("SOCKET", "RoomScreen: LaunchedEffect, message: $message")
        if (message.startsWith("Game started!")) {
            navController.navigate("setting-room")
        }
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
        Button(onClick = {
            viewModel.joinRoom(inputRoomCode);
        }) {
            Text("Join Room")
        }
        if (joinedRoom != "") {
            val roomId = joinedRoom.split(":")[1]
            Text(text = "Joined room: $roomId successfully")
        }
    }
}