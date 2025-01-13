package com.example.treasurehunter.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.treasurehunter.R
import com.example.treasurehunter.data.viewModel.TreasureViewModel

@Preview
@Composable
fun OpenChest() {
    val treasure by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.treasure)
    )

    val treasureProgress by animateLottieCompositionAsState(
        composition = treasure,
        iterations = 1,
    )

    LaunchedEffect ( key1 = treasureProgress ) {
        if (treasureProgress == 1f) {
            TreasureViewModel.hideChest()
        }
    }

    Box (
        modifier = Modifier.fillMaxSize()

    ) {
        // Background island
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.island),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Treasure animation
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = treasure,
                progress = treasureProgress,
                modifier = Modifier.size(200.dp),

            )
        }

    }
}
