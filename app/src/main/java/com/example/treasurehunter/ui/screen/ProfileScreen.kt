package com.example.treasurehunter.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.auth.AuthViewModel
import com.example.treasurehunter.R
import com.example.treasurehunter.data.model.User
import com.example.treasurehunter.ui.component.BackButton
import com.example.treasurehunter.LocalNavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen() {
    val navController = LocalNavController.current
    val currentUser by AuthViewModel.currentUser.collectAsState(initial = null)

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF5881F), Color(0xFFFFA726), Color.White)
                    )
                )
        ) {
            currentUser?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
//                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Nút Back và tiêu đề ở đầu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackButton(onBackPress = {
                            navController.popBackStack()
                        })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Ảnh đại diện
                    Image(
                        painter = painterResource(id = R.drawable.avatar_default), // Thay bằng ảnh đại diện mặc định
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .padding(4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Thông tin người chơi
                    Text(
                        text = user.fullName, // Tên người chơi
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Highest Score: ${user.highestScore}", // Điểm cao nhất
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card chứa thông tin chi tiết
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            ProfileInfoRow(label = "Name", value = user.fullName)
                            ProfileInfoRow(label = "Birthdate", value = user.dob.toFormattedString())
                            ProfileInfoRow(label = "Gender", value = user.gender.name)
                            ProfileInfoRow(label = "High Score", value = "${user.highestScore}")
                        }

                    }

                    Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống cuối màn hình

                    // Nút Logout
                    Button(
                        onClick = {
                            AuthViewModel.logoutUser()
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(20.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = "Logout",
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } ?: run {
                // Hiển thị trạng thái khi không có người dùng
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading user data...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
    }
}

fun Date.toFormattedString(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this)
}
