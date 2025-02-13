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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.data.viewModel.SocketViewModel
import com.example.treasurehunter.ui.component.BackButton

@Preview
@Composable
fun MultiplayerLobby() {
    val navController = LocalNavController.current
    val viewModel = SocketViewModel.room

    val roomId by viewModel.roomId
    val members by viewModel.members

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
                onClick = { viewModel.createRoom() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D2E),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Create Room",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (roomId != "") {
                Text( modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White),
                    fontSize = 20.sp,
                    text = "Room ID: $roomId")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.startGame(roomId, GameViewModel.gameRadius)
                        viewModel.inGame()
                        navController.navigate("in-game")},
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
                        text = "Start Game",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }



}