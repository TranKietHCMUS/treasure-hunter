package com.example.treasurehunter.data.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.app.auth.AuthViewModel
import com.example.treasurehunter.data.model.ScreenMode
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random


class PuzzleViewModel @Inject constructor() : ViewModel() {
    companion object {
        // Puzzle image
        val imageSize = 240.dp

        val imageUrlList = listOf(
            "https://hcmus.edu.vn/wp-content/uploads/2021/12/logo-khtn_remake-1.png",
            "https://tuyensinh.uit.edu.vn/intro/images/uit.png",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTlRxVxzJ_r6kqouMYfDStNMB9JGjdZHmL4grtHio-zky9prYZKZnObbngSHECDLx1rApA&usqp=CAU",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/HCMUT_official_logo.png/1200px-HCMUT_official_logo.png",
            "https://ump.edu.vn/uploads/ckeditor/images/LOGO-DHYD-400.jpg",
            "https://play-lh.googleusercontent.com/QvRr8HNSe07It4Gic6vu4zDDgcioRrBP9cEsuoc5x8HqXfd4TrK07v7w5SV_VBR1tdQ=w240-h480-rw?fbclid=IwZXh0bgNhZW0CMTAAAR0byMXbaOg6qREWw7Zv0qfNAr70xocylfwbyq0GyAjN-1lBnqVVIwHi-JQ_aem_6ilb8CSDA5mdA4twvytE9g",
            "https://scontent.fvca1-3.fna.fbcdn.net/v/t39.30808-6/455404073_10169003305875103_8199756517617490402_n.jpg?_nc_cat=111&ccb=1-7&_nc_sid=bd9a62&_nc_eui2=AeELiy_TrOEI6rZh-8O2JkAx-_Yex0inJXX79h7HSKcldfI6VIHAdDc0biNKsMht-YaptOu70mvKKnFPaEm84_KU&_nc_ohc=kA9mLvyIb_4Q7kNvgFg5Jaa&_nc_oc=AdjJdf6cO9pEigzopoiKP5V7wV4w5mSkUIu6X11TcVB5U9BxC2pNXNEw53n1nMVM6cF9yiHJspGU14wGjNkS4kVI&_nc_zt=23&_nc_ht=scontent.fvca1-3.fna&_nc_gid=AkFBSjXcF95IWd0476SgKmq&oh=00_AYBHzyf57MDokqSlkCv5baKAjYjzDzsH9WYsWsbw0ahzDQ&oe=67B66545",
            "https://upload.wikimedia.org/wikipedia/vi/3/30/Logo-NEU.PNG?fbclid=IwZXh0bgNhZW0CMTAAAR1V7AQPNGEnAw_UkA4Ryf-SsVTTTSUoQHtN63wn8hg1Mu_ZAMJJw-OZSC8_aem_WmRm19HQGywwFT7h8vQN2g",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTu1pHCvJl9Slve269G4BbuU_kYHvQHTdqrKA&s&fbclid=IwZXh0bgNhZW0CMTAAAR0nY84sOZxEI8RccxIfqdD3P87J-tGCJQewEQxm7hzlToAUa4as0gGKfyk_aem_X_4dYDnVXjkJ6qN8tiQqrQ",
        )

        // Puzzle result
        var userInput by mutableStateOf("")
        val resultList = listOf(
            "HCMUS",
            "UIT",
            "UEL",
            "HCMUT",
            "UPM",
            "UEH",
            "UFM",
            "NEU",
            "HUB",
        )

        var imageId by mutableStateOf(0)
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

        fun showAllPieces() {
            for (i in images.indices) {
                for (j in images[i].indices) {
                    images[i][j] = Color.Transparent
                }
            }
        }
        var correctAnswer by mutableStateOf(resultList[0])

        var isSolved by mutableStateOf(false)
        var remainSubmition by mutableStateOf(3)

        fun init() {
            images = Array(3) { Array(3) { Color.Gray } }

            userInput = ""
            isSolved = false
            remainSubmition = 3

            ScoreViewModel.score = 0

            SocketViewModel.room.roomId.value = ""
            SocketViewModel.room.message.value = ""
            SocketViewModel.room.joinedRoom.value = ""
            SocketViewModel.room.members.value = ""

            GameViewModel.changeScreenMode(ScreenMode.MAP)
        }

        fun checkAnswer() {
            if (userInput == correctAnswer) {
                isSolved = true
                ScoreViewModel.score += (9 - ScoreViewModel.score) * (remainSubmition + 1)
                showAllPieces()
                val roomId by SocketViewModel.room.roomId
                SocketViewModel.room.endGame(roomId)
            } else {
                remainSubmition--
                userInput = ""

                if (remainSubmition == 0) {
                    showAllPieces()
                    isSolved = true
                }
            }

            AuthViewModel.updateHighestScore(ScoreViewModel.score)
        }

        fun setImage(imageIdProp: Int) {
            imageId = imageIdProp
            imageUrl = imageUrlList[imageIdProp]
            correctAnswer = resultList[imageIdProp]
        }
    }
}