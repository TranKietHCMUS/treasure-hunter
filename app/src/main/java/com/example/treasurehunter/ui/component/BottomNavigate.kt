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
    val mode : ScreenMode,
    val size : Int,
    val padding : Int
)

@Composable
fun BottomNavigate() {
    val navItemList = listOf(
        NavItem("Map", lottieResId = R.raw.map_animation, mode = ScreenMode.MAP, size = 48, padding = 1),
        NavItem("AR", lottieResId = R.raw.ar_animation, mode = ScreenMode.AR, size = 44, padding = 3),
        NavItem("Puzzle", lottieResId = R.raw.puzzle_animation, mode = ScreenMode.PUZZLE, size = 50, padding = 0),
    )

    NavigationBar (
        modifier = Modifier.padding(0.dp),
    ) {
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
                        modifier = Modifier.size(navItem.size.dp).padding(navItem.padding.dp)
                    )
                       },
                label = {
                    Text(
                        navItem.label,
                        modifier = Modifier.padding(0.dp)
                    )
                        },
                selected = (navItem.mode == GameViewModel.screenMode),
                onClick = {
                    GameViewModel.changeScreenMode(navItem.mode)
                }
            )
        }
    }
}