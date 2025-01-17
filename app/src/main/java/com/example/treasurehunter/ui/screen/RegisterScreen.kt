package com.example.app.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.treasurehunter.LocalNavController
import com.example.treasurehunter.data.model.Gender
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    val navController = LocalNavController.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // DatePicker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.time ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Kiểm tra nếu ngày được chọn không phải trong tương lai
                            if (millis <= System.currentTimeMillis()) {
                                selectedDate = Date(millis)
                                showDatePicker = false
                            } else {
                                errorMessage = "Ngày sinh không thể là ngày trong tương lai"
                            }
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Hủy")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Họ và tên") },
            isError = fullName.isNotEmpty() && fullName.length < 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Gender selection
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = {},
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Giới tính: ")
                RadioButton(
                    selected = selectedGender == Gender.MALE,
                    onClick = { selectedGender = Gender.MALE }
                )
                Text("Nam")
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = selectedGender == Gender.FEMALE,
                    onClick = { selectedGender = Gender.FEMALE }
                )
                Text("Nữ")
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = selectedGender == Gender.OTHER,
                    onClick = { selectedGender = Gender.OTHER }
                )
                Text("Khác")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Date of Birth field with DatePicker
        OutlinedTextField(
            value = selectedDate?.let { dateFormat.format(it) } ?: "",
            onValueChange = { },
            label = { Text("Ngày sinh") },
            trailingIcon = {
                IconButton(onClick = {
                    errorMessage = ""
                    showDatePicker = true
                }) {
                    Icon(Icons.Default.DateRange, "Chọn ngày")
                }
            },
            readOnly = true,
            enabled = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            isError = password.isNotEmpty() && password.length < 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    val dob = selectedDate ?: throw Exception("Vui lòng chọn ngày sinh")

                    when {
                        fullName.length < 2 -> throw Exception("Tên phải có ít nhất 2 ký tự")
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> throw Exception("Email không hợp lệ")
                        password.length < 6 -> throw Exception("Mật khẩu phải có ít nhất 6 ký tự")
                        else -> {
                            AuthViewModel.registerUser(
                                email = email,
                                password = password,
                                fullName = fullName,
                                gender = selectedGender,
                                dob = dob,
                                onSuccess = onRegisterSuccess,
                                onError = { errorMessage = it }
                            )
                        }
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Đã có lỗi xảy ra"
                }
            },
            enabled = fullName.isNotEmpty() && email.isNotEmpty() &&
                    password.isNotEmpty() && selectedDate != null
        ) {
            Text("Đăng ký")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red)
        }

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Đã có tài khoản? Đăng nhập ngay!")
        }
    }
}