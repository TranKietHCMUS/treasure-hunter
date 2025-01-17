package com.example.app.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.treasurehunter.LocalNavController
import com.example.treasurehunter.ui.component.Logo

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val navController = LocalNavController.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Logo()

                Text(
                    "Đăng nhập",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = password.isNotEmpty() && password.length < 6
                )

                Button(
                    onClick = {
                        AuthViewModel.loginUser(
                            email,
                            password,
                            onSuccess = { onLoginSuccess() },
                            onError = { errorMessage = it }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = email.isNotEmpty() && password.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D2E),
                    ),
                ) {
                    Text("Đăng nhập")
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Chưa có tài khoản? Đăng ký ngay!")
                }
            }
        }
    }
}