package com.example.treasurehunter

import MapScreen
import android.app.Application
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.auth.LoginScreen
import com.example.app.auth.RegisterScreen
import com.example.treasurehunter.data.viewModel.SocketViewModel
import com.example.treasurehunter.data.viewModel.TreasureViewModel
import com.example.treasurehunter.ui.component.OpenChest
import com.example.treasurehunter.ui.screen.ARScreen
import com.example.treasurehunter.ui.screen.HomeScreen
import com.example.treasurehunter.ui.screen.InGameScreen
import com.example.treasurehunter.ui.screen.JoinRoomScreen
import com.example.treasurehunter.ui.screen.MultiplayerLobby
import com.example.treasurehunter.ui.screen.ProfileScreen
import com.example.treasurehunter.ui.screen.RoomControlScreen
import com.example.treasurehunter.ui.screen.RoomScreen
import com.example.treasurehunter.ui.screen.SettingRoomScreen
//import com.example.treasurehunter.ui.theme.TreasureHunterTheme
import com.example.treasurehunter.ui.screen.TestScreen
import com.example.treasurehunter.ui.theme.TreasureHunterTheme
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FacebookSdk.sdkInitialize(applicationContext)
        // Application-level initialization can go here
    }
}

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
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

    Log.i("SOCKET", "RoomScreen: LaunchedEffect")
    SocketViewModel.room.connectToServer(BuildConfig.IP, BuildConfig.PORT)

    Box(modifier = modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(
                navController = navController,
                startDestination = "home",
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
                composable("setting-room") { SettingRoomScreen() }
                composable("in-game") { InGameScreen() }
                composable("login") { LoginScreen() { navController.navigate("home") } }
                composable("register") { RegisterScreen() { navController.navigate("login") } }
                composable("profile") { ProfileScreen() }
                composable("room") { RoomScreen() }
                composable("control-room") { RoomControlScreen() }
                composable("join-room") { JoinRoomScreen() }
                composable("multiplayer-lobby") { MultiplayerLobby() }


            }
        }

        if (TreasureViewModel.isChestShow) {
            Log.i("MainActivity", "Chest is shown")
            OpenChest()
        }
    }
}