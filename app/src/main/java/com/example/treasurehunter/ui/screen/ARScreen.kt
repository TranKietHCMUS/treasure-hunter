package com.example.treasurehunter.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.example.treasurehunter.geospatial.ARActivity

val onRunClick: (android.content.Context) -> Unit = { context ->
    val intent = Intent(context, ARActivity::class.java)
    context.startActivity(intent)
}

@Preview
@Composable
fun ARScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { onRunClick(context) }) {
            Text("Run")
        }
    }
}
