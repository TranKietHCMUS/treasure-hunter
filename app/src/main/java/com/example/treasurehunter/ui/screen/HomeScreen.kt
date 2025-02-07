package com.example.treasurehunter.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.treasurehunter.R
import com.example.treasurehunter.LocalNavController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.treasurehunter.data.viewModel.PuzzleViewModel

@Preview
@Composable
fun HomeScreen() {
    val navController = LocalNavController.current // Sử dụng để điều hướng giữa các màn hình.

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5881F), Color(0xFFFFA726), Color.White)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                // Điều hướng đến màn hình Profile
                navController.navigate("profile")
            },
            modifier = Modifier
                .align(Alignment.TopEnd) // Căn góc trên bên phải
                .padding(16.dp) // Khoảng cách từ góc
        ) {
            Icon(
                painter = painterResource(id = R.drawable.account_circle), // Thay bằng icon hồ sơ của bạn
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(32.dp) // Kích thước icon
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo
            Image(
                painter = painterResource(R.drawable.logotitle), // Thay bằng logo của bạn.
                contentDescription = "Logo",
                modifier = Modifier
                    .size(270.dp) // Kích thước logo
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Start Button
            Button(
                onClick = {
//                     Điều hướng đến màn hình CreateRoom
                    PuzzleViewModel.init()
                    navController.navigate("control-room")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = "START",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}