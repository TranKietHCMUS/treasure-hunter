package com.example.treasurehunter.ui.screen

import MapScreen
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.treasurehunter.LocalNavController

data class NavItem(
    val label : String,
    val icon : ImageVector,
)

@Preview
@Composable
fun InGameScreen() {
    val navItemList = listOf(
        NavItem("Map", Icons.Default.Home),
        NavItem("AR", Icons.Filled.Person),
        NavItem("Test", Icons.Filled.Create),
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        icon = { Icon(navItem.icon, contentDescription = "Icon") },
                        label = { Text(navItem.label) },
                        selected = index == 0,
                        onClick = {
                            selectedIndex = index
                        }
                    )
                }
            }
        }
    ) {
        innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex
        )
    }


}

@Composable
fun ContentScreen(modifier: Modifier, selectedIndex: Int) {
    when (selectedIndex) {
        0 -> MapScreen(modifier)
        1 -> ARScreen()
        2 -> TestScreen()
    }
}
