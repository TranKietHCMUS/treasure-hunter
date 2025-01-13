package com.example.treasurehunter.ui.screen

import MapScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.treasurehunter.data.model.ScreenMode
import com.example.treasurehunter.data.viewModel.InGameViewModel

data class NavItem(
    val label : String,
    val icon : ImageVector,
    val mode : ScreenMode
)

@Preview
@Composable
fun InGameScreen() {
    val navItemList = listOf(
        NavItem("Map", Icons.Default.Home, ScreenMode.MAP),
        NavItem("AR", Icons.Filled.Person, ScreenMode.AR),
        NavItem("Puzzle", Icons.Filled.Create, ScreenMode.PUZZLE),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEach { navItem ->
                    NavigationBarItem(
                        icon = { Icon(navItem.icon, contentDescription = "Icon") },
                        label = { Text(navItem.label) },
                        selected = (navItem.mode == InGameViewModel.screenMode),
                        onClick = {
                            InGameViewModel.changeScreenMode(navItem.mode)
                        }
                    )
                }
            }
        }
    ) {
        innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
        )
    }


}

@Composable
fun ContentScreen(modifier: Modifier) {
    when (InGameViewModel.screenMode) {
        ScreenMode.MAP -> MapScreen(modifier)
        ScreenMode.AR -> ARScreen()
        ScreenMode.PUZZLE -> TestScreen()
    }
}
