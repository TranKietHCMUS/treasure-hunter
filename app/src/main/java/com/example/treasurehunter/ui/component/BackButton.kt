package com.example.treasurehunter.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.treasurehunter.R

@Composable
fun BackButton(onBackPress: () -> Unit) {
    IconButton(
        onClick = { onBackPress() },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "Back",
            tint = Color.Black
        )
    }
}