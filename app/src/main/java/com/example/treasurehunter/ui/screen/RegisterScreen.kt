package com.example.app.auth

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

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    val navController = LocalNavController.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Tạo FocusRequester cho mỗi ô nhập liệu
    val fullNameFocusRequester = FocusRequester()
    val genderFocusRequester = FocusRequester()
    val birthDateFocusRequester = FocusRequester()
    val emailFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Full Name TextField
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.focusRequester(fullNameFocusRequester)
                .onFocusChanged {
                    if (!it.hasFocus) {
                        genderFocusRequester.requestFocus()
                    }
                }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Gender TextField
        TextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Gender") },
            modifier = Modifier.focusRequester(genderFocusRequester)
                .onFocusChanged {
                    if (!it.hasFocus) {
                        birthDateFocusRequester.requestFocus()
                    }
                }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Birth Date TextField
        TextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Birth Date (YYYY-MM-DD)") },
            modifier = Modifier.focusRequester(birthDateFocusRequester)
                .onFocusChanged {
                    if (!it.hasFocus) {
                        emailFocusRequester.requestFocus()
                    }
                }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Email TextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.focusRequester(emailFocusRequester)
                .onFocusChanged {
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
                        // Khi nhấn Enter thì đăng ký
                        AuthViewModel.registerUser(email, password, fullName, gender, birthDate, onSuccess = {
                            onRegisterSuccess()
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

        // Register Button
        Button(onClick = {
            AuthViewModel.registerUser(email, password, fullName, gender, birthDate, onSuccess = {
                onRegisterSuccess()
            }, onError = {
                errorMessage = it
            })
        }) {
            Text("Register")
        }

        // Hiển thị thông báo lỗi nếu có
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red)
        }

        // Dẫn đến màn hình đăng nhập
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Đã có tài khoản? Đăng nhập ngay!")
        }
    }
}
