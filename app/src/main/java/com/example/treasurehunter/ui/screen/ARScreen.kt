package com.example.treasurehunter.ui.screen

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.example.treasurehunter.geospatial.ARActivity

@Preview
@Composable
fun ARScreen() {
    val context = LocalContext.current

    val intent = Intent(context, ARActivity::class.java)
    context.startActivity(intent)
}
