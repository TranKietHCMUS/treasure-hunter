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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.data.viewModel.PuzzleViewModel
import com.example.treasurehunter.data.viewModel.SocketViewModel
import com.example.treasurehunter.ui.component.BackButton
import com.example.treasurehunter.ui.component.InfoBox
import com.example.treasurehunter.ui.component.Loading
import com.example.treasurehunter.ui.component.Logo
import com.example.treasurehunter.ui.component.ScrollableBox

@Preview
@Composable
fun MultiplayerLobby() {
    val navController = LocalNavController.current
    val viewModel = SocketViewModel.room

    val roomId by viewModel.roomId
    val members by viewModel.members

    var isLoading by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val topPadding = screenHeight / 3

    LaunchedEffect (roomId) {
        viewModel.waitingMembers()
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Column (
                modifier = Modifier.height(topPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Logo()
            }

            CreateButton(
                isLoading = isLoading,
                enabled = roomId == "",
                onClick = {
                    viewModel.createRoom()
                },
                myText = "Create Room"
            )

            Spacer(modifier = Modifier.height(20.dp))

            InfoBox("Room ID: $roomId")

            if (roomId != "") {
                Spacer(modifier = Modifier.height(20.dp))

                CreateButton(
                    isLoading = isLoading,
                    enabled = true,
                    onClick = {
                        isLoading = true
                        viewModel.startGame(roomId, GameViewModel.gameRadius, PuzzleViewModel.imageId)

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

                Spacer(modifier = Modifier.height(20.dp))

                ScrollableBox("Members:", members)

                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        if (isLoading) {
            Loading()
        }
    }



}