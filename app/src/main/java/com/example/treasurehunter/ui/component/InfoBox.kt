package com.example.treasurehunter.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight

@Composable
fun InfoBox(text: String) {
    Box(
        modifier = Modifier
            .size(350.dp, 50.dp) // Kích thước hộp
            .background(Color.White, shape = RoundedCornerShape(16.dp)) // Màu nền xám và bo góc
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .align(Alignment.Center) // Căn giữa văn bản
                .padding(3.dp) // Thêm khoảng cách xung quanh văn bản
        )
    }
}