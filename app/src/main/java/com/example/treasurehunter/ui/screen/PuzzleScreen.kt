package com.example.treasurehunter.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import com.example.treasurehunter.LocalNavController
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.data.viewModel.ScoreViewModel

@Composable
fun InputComponent() {
    Text(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 5.dp, bottom = 5.dp),
        text = "You have ${PuzzleViewModel.remainSubmition} submition${if(PuzzleViewModel.remainSubmition > 1) "s" else ""} left",
        color = Color.White,
        fontSize = 10.sp
    )

    Text(
        text = "Result includes ${PuzzleViewModel.correctAnswer.length} characters",
        color = Color.White,
        fontSize = 15.sp
    )

    Spacer(modifier = Modifier.height(10.dp))

    TextField(
        value = PuzzleViewModel.userInput, onValueChange = {
            var submit = false;
            if (it.length > 1 && it[it.length - 1] == '\n') {
                submit = true
            }
            if (it.length <= PuzzleViewModel.correctAnswer.length) {
                PuzzleViewModel.userInput = it.uppercase()
            }
            if (submit) {
                Log.i("Correct Answer", PuzzleViewModel.correctAnswer)
                Log.i("User Input", PuzzleViewModel.userInput)
                // Check the user's input
                PuzzleViewModel.checkAnswer()
            }
        },
    )

    Spacer(modifier = Modifier.height(10.dp))

    Button(onClick = {
        Log.i("Correct Answer", PuzzleViewModel.correctAnswer)
        Log.i("User Input", PuzzleViewModel.userInput)
        // Check the user's input
        PuzzleViewModel.checkAnswer()
    }) {
        Text("Submit")
    }
}

@Composable
fun SolvedCoponent() {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "The hidden treasure:",
            color = Color.White,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "${PuzzleViewModel.correctAnswer}",
            color = Color.White,
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your max score: ${ScoreViewModel.maxScore}",
            color = Color.White,
            fontSize = 12.sp
        )

        Button(onClick = {
            navController.navigate("create-room")
        }) {
            Text("New game")
        }
    }
}

@Preview
@Composable
fun PuzzleScreen() {
    Column  (
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF260000)
            ),

        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your current score: ",
            color = Color.White,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = ScoreViewModel.score.toString(),
            color = Color.White,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF643000)
                )
        ) {
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .size(PuzzleViewModel.imageSize)
                        .background(
                            Color(0xFFFFFFFF)
                        )
                )
            }

            Column (
                modifier = Modifier.fillMaxWidth(),
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

//             Show grid 3x3 images here
            Column (
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until 3) {
                    Row {
                        for (j in 0 until 3) {
                            // Hiển thị hình ảnh cho mỗi ô
                            Box(
                                modifier = Modifier
                                    .size(PuzzleViewModel.imageSize / 3)
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

        Spacer(modifier = Modifier.height(5.dp))

        if (PuzzleViewModel.isSolved) {
            SolvedCoponent()
        } else {
            InputComponent()
        }
    }
}
