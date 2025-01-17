package com.example.treasurehunter

import MapScreen
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.auth.AuthViewModel
import com.example.app.auth.LoginScreen
import com.example.app.auth.RegisterScreen
import com.example.treasurehunter.ui.screen.CreateRoomScreen
import com.example.treasurehunter.data.viewModel.TreasureViewModel
import com.example.treasurehunter.ui.component.OpenChest
import com.example.treasurehunter.geospatial.GeospatialActivity
import com.example.treasurehunter.ui.screen.ARScreen
import com.example.treasurehunter.ui.screen.HomeScreen
import com.example.treasurehunter.ui.screen.InGameScreen
//import com.example.treasurehunter.ui.theme.TreasureHunterTheme
import com.example.treasurehunter.ui.screen.TestScreen
import com.example.treasurehunter.ui.theme.TreasureHunterTheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlin.math.log

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Application-level initialization can go here
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TreasureHunterTheme {
                MyApp()
            }
        }
    }
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("NavController is not provided")
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Box(modifier = modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(
                navController = navController,
                startDestination = "login",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.systemBars.asPaddingValues())
            ) {
                composable("home") { HomeScreen() }
                composable("map") { MapScreen() }
                composable("test") { TestScreen() }
                composable("ar") { ARScreen() }
                composable("create-room") { CreateRoomScreen() }
                composable("in-game") { InGameScreen() }
                composable("login") { LoginScreen() { navController.navigate("home") } }
                composable("register") { RegisterScreen() { navController.navigate("login") } }
            }
        }

        if (TreasureViewModel.isChestShow) {
            Log.i("MainActivity", "Chest is shown")
            OpenChest()
        }
    }
}