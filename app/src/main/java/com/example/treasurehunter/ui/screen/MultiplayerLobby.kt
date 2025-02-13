package com.example.treasurehunter.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.treasurehunter.LocalNavController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.data.viewModel.SocketViewModel
import com.example.treasurehunter.ui.component.BackButton
import com.example.treasurehunter.ui.component.Loading

@Preview
@Composable
fun MultiplayerLobby() {
    val navController = LocalNavController.current
    val viewModel = SocketViewModel.room

    val roomId by viewModel.roomId
    val members by viewModel.members

    var isLoading by remember { mutableStateOf(false) }

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

            CreateButton(
                isLoading = isLoading,
                enabled = true,
                onClick = {
                    viewModel.createRoom()
                },
                myText = "Create Room"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (roomId != "") {
                Text( modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White),
                    fontSize = 20.sp,
                    text = "Room ID: $roomId")

                Spacer(modifier = Modifier.height(16.dp))

                CreateButton(
                    isLoading = isLoading,
                    enabled = true,
                    onClick = {
                        isLoading = true
                        viewModel.startGame(roomId, GameViewModel.gameRadius)

                        while (true) {
                            if (GameViewModel.gameLocation != null) {
                                viewModel.inGame()
                                isLoading = false
                                navController.navigate("in-game")
                                break
                            }
                        }
                    },
                    myText = "Start Game"
                )
            }
        }

        if (isLoading) {
            Loading()
        }
    }



}