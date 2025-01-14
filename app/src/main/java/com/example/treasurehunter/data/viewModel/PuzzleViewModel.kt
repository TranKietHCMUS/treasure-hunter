package com.example.treasurehunter.data.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import kotlin.random.Random


class PuzzleViewModel @Inject constructor() : ViewModel() {
    companion object {
        val imageSize = 300.dp

        private val imageUrlList = listOf(
            "https://hcmus.edu.vn/wp-content/uploads/2021/12/logo-khtn_remake-1.png"
        )
        var imageUrl by mutableStateOf(imageUrlList[0])
        var images by mutableStateOf(Array(3) { Array(3) { Color.Gray } })

        fun init() {
            images = Array(3) { Array(3) { Color.Gray } }

            val randomIndex = imageUrlList.indices.random()
            imageUrl = imageUrlList[randomIndex]
        }

        fun showPiece() {
            val grayPieces = mutableListOf<Pair<Int, Int>>()

            for (i in images.indices) {
                for (j in images[i].indices) {
                    if (images[i][j] == Color.Gray) {
                        grayPieces.add(Pair(i, j))
                    }
                }
            }

            if (grayPieces.isNotEmpty()) {
                val randomIndex = Random.nextInt(grayPieces.size)
                val selectedPiece = grayPieces[randomIndex]

                images[selectedPiece.first][selectedPiece.second] = Color.Transparent
            }
        }
    }
}