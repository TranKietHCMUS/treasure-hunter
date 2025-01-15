package com.example.app.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import com.example.treasurehunter.LocalNavController

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val navController = LocalNavController.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Tạo FocusRequester cho mỗi ô nhập liệu
    val emailFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email TextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .focusRequester(emailFocusRequester)
                .onFocusChanged {
                    // Nếu mất focus thì chuyển đến ô tiếp theo
                    if (!it.hasFocus) {
                        passwordFocusRequester.requestFocus()
                    }
                }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password TextField
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .focusRequester(passwordFocusRequester)
                .onKeyEvent {
                    if (it.key == Key.Enter) {
                        // Khi nhấn Enter thì đăng nhập
                        AuthViewModel.loginUser(email, password, onSuccess = {
                            onLoginSuccess()
                        }, onError = {
                            errorMessage = it
                        })
                        true
                    } else {
                        false
                    }
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(onClick = {
            AuthViewModel.loginUser(email, password, onSuccess = {
                onLoginSuccess()
            }, onError = {
                errorMessage = it
            })
        }) {
            Text("Login")
        }

        // Hiển thị thông báo lỗi nếu có
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red)
        }

        // Dẫn đến màn hình đăng ký
        TextButton(onClick = { navController.navigate("register") }) {
            Text("Chưa có tài khoản, hãy đăng ký!")
        }
    }
}
