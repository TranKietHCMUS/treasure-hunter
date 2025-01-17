package com.example.treasurehunter.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.treasurehunter.data.model.ScreenMode
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.R

data class NavItem(
    val label : String,
    val lottieResId : Int,
    val mode : ScreenMode
)

@Composable
fun BottomNavigate() {
    val navItemList = listOf(
        NavItem("Map", lottieResId = R.raw.ar_animation, mode = ScreenMode.MAP),
        NavItem("AR", lottieResId = R.raw.ar_animation, mode = ScreenMode.AR),
        NavItem("Puzzle", lottieResId = R.raw.ar_animation, mode = ScreenMode.PUZZLE),
        NavItem("Test", lottieResId = R.raw.ar_animation, mode = ScreenMode.TEST),
    )

    NavigationBar {
        navItemList.forEach { navItem ->
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(navItem.lottieResId ?: 0))
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = 1,
                isPlaying = (navItem.mode == GameViewModel.screenMode)
            )

            NavigationBarItem(
                icon = {
                    LottieAnimation(
                        composition,
                        progress,
                        modifier = Modifier.size(50.dp).padding(5.dp)
                    )
                       },
                label = { Text(navItem.label) },
                selected = (navItem.mode == GameViewModel.screenMode),
                onClick = {
                    GameViewModel.changeScreenMode(navItem.mode)
                }
            )
        }
    }
}