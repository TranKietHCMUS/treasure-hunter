package com.example.treasurehunter.data.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.app.auth.AuthViewModel
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random


class PuzzleViewModel @Inject constructor() : ViewModel() {
    companion object {
        // Puzzle image
        val imageSize = 240.dp

        private val imageUrlList = listOf(
            "https://hcmus.edu.vn/wp-content/uploads/2021/12/logo-khtn_remake-1.png"
        )
        var imageUrl by mutableStateOf(imageUrlList[0])
        var images by mutableStateOf(Array(3) { Array(3) { Color.Gray } })


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

        // Puzzle result
        var userInput by mutableStateOf("")
        private val resultList = listOf(
            "HCMUS"
        )
        var correctAnswer by mutableStateOf(resultList[0])

        var isSolved by mutableStateOf(false)
        var remainSubmition by mutableStateOf(3)

        fun init() {
            images = Array(3) { Array(3) { Color.Gray } }

            val randomIndex = imageUrlList.indices.random()
            imageUrl = imageUrlList[randomIndex]
            correctAnswer = resultList[randomIndex]
            userInput = ""
            isSolved = false
            remainSubmition = 3

            ScoreViewModel.maxScore = AuthViewModel.currentUser.value?.highestScore ?: 0
        }

        fun checkAnswer() {
            if (userInput == correctAnswer) {
                isSolved = true
                ScoreViewModel.score += (9 - ScoreViewModel.score) * (remainSubmition + 1)
                ScoreViewModel.maxScore = max(ScoreViewModel.maxScore, ScoreViewModel.score)
                AuthViewModel.updateHighestScore(ScoreViewModel.maxScore)
            } else {
                remainSubmition--
                userInput = ""
            }
        }
    }
}