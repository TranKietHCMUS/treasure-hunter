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
import com.example.treasurehunter.ui.component.BottomNavigate

data class NavItem(
    val label : String,
    val icon : ImageVector,
    val mode : ScreenMode
)

@Preview
@Composable
fun InGameScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigate()
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
