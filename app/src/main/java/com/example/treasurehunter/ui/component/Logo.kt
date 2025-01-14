package com.example.treasurehunter.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.treasurehunter.R

@Composable
fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logotitle),
        contentDescription = "Logo",
        modifier = Modifier
            .size(250.dp)
    )
}