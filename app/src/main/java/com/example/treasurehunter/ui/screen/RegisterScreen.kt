package com.example.app.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ArrowDropDown
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
    var isGenderMenuExpanded by remember { mutableStateOf(false) }
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
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        if (millis <= System.currentTimeMillis()) {
                            selectedDate = Date(millis)
                            showDatePicker = false
                        } else {
                            errorMessage = "Ngày sinh không thể là ngày trong tương lai"
                        }
                    }
                }) {
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
                Text(
                    "Đăng ký tài khoản",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = fullName.isNotEmpty() && fullName.length < 2
                )

                // Gender Dropdown
                ExposedDropdownMenuBox(
                    expanded = isGenderMenuExpanded,
                    onExpandedChange = { isGenderMenuExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = when(selectedGender) {
                            Gender.MALE -> "Nam"
                            Gender.FEMALE -> "Nữ"
                            Gender.OTHER -> "Khác"
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "Dropdown arrow")
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Giới tính") }
                    )
                    ExposedDropdownMenu(
                        expanded = isGenderMenuExpanded,
                        onDismissRequest = { isGenderMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Chọn") },
                            onClick = {
                                selectedGender = Gender.MALE
                                isGenderMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nữ") },
                            onClick = {
                                selectedGender = Gender.FEMALE
                                isGenderMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Khác") },
                            onClick = {
                                selectedGender = Gender.OTHER
                                isGenderMenuExpanded = false
                            }
                        )
                    }
                }

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
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = fullName.isNotEmpty() && email.isNotEmpty() &&
                            password.isNotEmpty() && selectedDate != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D2E),
                    ),
                ) {
                    Text("Đăng ký")
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Đã có tài khoản? Đăng nhập ngay!")
                }
            }
        }
    }
}