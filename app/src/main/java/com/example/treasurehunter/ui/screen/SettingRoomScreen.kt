package com.example.treasurehunter.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.treasurehunter.LocalNavController
import com.example.treasurehunter.data.viewModel.GameViewModel
import com.example.treasurehunter.data.viewModel.PuzzleViewModel
import com.example.treasurehunter.ui.component.BackButton
import com.example.treasurehunter.ui.component.Loading
import com.example.treasurehunter.ui.component.Logo
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

@Composable
fun SettingRoomScreen() {
    var selectedRadius by remember { mutableStateOf<Double?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isMultiplayer by remember { mutableStateOf<Boolean?>(null) }

    val navController = LocalNavController.current
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5881F), Color(0xFFF5881F), Color.White)
                )
            )
    ) {
        BackButton(onBackPress = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Logo()
            Title(text = "Create Room")

            MultiplayerSelection(isMultiplayer) { isMultiplayer = it }

            RadiusSelection(selectedRadius = selectedRadius) { selectedRadius = it }

            Spacer(modifier = Modifier.height(24.dp))
            CreateButton(
                isLoading = isLoading,
                enabled = selectedRadius != null && isMultiplayer != null,
                onClick = {
                    if (hasLocationPermission) {
                        isLoading = true
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            object : CancellationToken() {
                                override fun onCanceledRequested(listener: OnTokenCanceledListener) = CancellationTokenSource().token
                                override fun isCancellationRequested() = false
                            }
                        ).addOnSuccessListener { location ->
                            isLoading = false
                            location?.let {
                                val position = LatLng(it.latitude, it.longitude)
                                currentLocation = position

                                // Set game location and radius
                                GameViewModel.setGameLocation(currentLocation!!)
                                GameViewModel.setGameRadius(selectedRadius!!)

                                // Generate random locations
                                GameViewModel.generateTreasures(currentLocation!!, selectedRadius!!)

                                // Navigate to InGameScreen
                                if (isMultiplayer == true) {
                                    navController.navigate("multiplayer-lobby")
                                } else {
                                    navController.navigate("in-game")
                                }
                            }
                        }.addOnFailureListener {
                            isLoading = false
                        }
                    } else {
                        // Điều hướng về RoomControlScreen nếu quyền chưa được cấp
                        navController.navigate("room-control")
                    }
                }
            )
        }

        if (isLoading) {
            Loading()
        }
    }
}


@Composable
fun Title(text: String) {
    Text(
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun RadiusSelection(selectedRadius: Double?, onRadiusSelected: (Double) -> Unit) {
    Text(
        text = "Select Radius",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 26.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val radii = listOf(
            Pair("200m", 200.0),
            Pair("500m", 500.0),
            Pair("1km", 1000.0),
        )

        radii.forEach { (text, value) ->
            RadiusButton(
                text = text,
                isSelected = selectedRadius == value,
                onClick = { onRadiusSelected(value) }
            )
        }
    }
}

@Composable
fun RadiusButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFF6D2E) else Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun CreateButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    myText: String = "Next"
) {
    Button(
        onClick = {
            if (!isLoading) onClick()
        },
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6D2E),
            disabledContainerColor = Color.Gray
        ),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .width(200.dp)
            .height(50.dp)
    ) {
        Text(
            text = myText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun MultiplayerSelection(selectedMode: Boolean?, onSelection: (Boolean) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "Game Mode",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 26.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            ModeButton(text = "Solo", isSelected = selectedMode == false, onClick = { onSelection(false) })
            ModeButton(text = "Multiplayer", isSelected = selectedMode == true, onClick = { onSelection(true) })
        }
    }
}

@Composable
fun ModeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFF6D2E) else Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}
