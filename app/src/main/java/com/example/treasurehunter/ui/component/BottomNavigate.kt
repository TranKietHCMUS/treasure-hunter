package com.example.treasurehunter.ui.component

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
import com.example.treasurehunter.data.model.ScreenMode
import com.example.treasurehunter.data.viewModel.InGameViewModel
import com.example.treasurehunter.ui.screen.NavItem

@Composable
fun BottomNavigate() {
    val navItemList = listOf(
        NavItem("Map", Icons.Default.Home, ScreenMode.MAP),
        NavItem("AR", Icons.Filled.Person, ScreenMode.AR),
        NavItem("Puzzle", Icons.Filled.Create, ScreenMode.PUZZLE),
        NavItem("Test", Icons.Filled.Add, ScreenMode.TEST),
    )

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