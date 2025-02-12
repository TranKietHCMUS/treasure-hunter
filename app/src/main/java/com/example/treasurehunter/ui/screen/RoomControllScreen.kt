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
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun RoomControlScreen() {
    val navController = LocalNavController.current

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
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            onClick = {
                navController.navigate("setting-room")
            }
        ) {
            Text("Create room")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            onClick = {
                navController.navigate("join-room")
            }
        ) {
            Text("Join room")
        }
    }
}