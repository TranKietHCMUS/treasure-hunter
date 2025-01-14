package com.example.treasurehunter.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.treasurehunter.data.viewModel.PuzzleViewModel
import com.example.treasurehunter.ui.theme.Orange


@Preview
@Composable
fun PuzzleScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF643000)
            )
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberImagePainter(PuzzleViewModel.imageUrl),
                contentDescription = "Puzzle",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(PuzzleViewModel.imageSize)
            )
        }

        // Show grid 3x3 images here
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in 0 until 3) {
                Row {
                    for (j in 0 until 3) {
                        // Hiển thị hình ảnh cho mỗi ô
                        Box(
                            modifier = Modifier
                                .size(PuzzleViewModel.imageSize / 3 - 4.dp)
                                .background(
                                    if (PuzzleViewModel.images[i][j] == Color.Gray)
                                        Color.White else Color.Transparent
                                )
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier =  Modifier
                                    .fillMaxSize()
                                    .background(PuzzleViewModel.images[i][j])
                            ){}
                        }
                    }
                }
            }
        }

    }
}
