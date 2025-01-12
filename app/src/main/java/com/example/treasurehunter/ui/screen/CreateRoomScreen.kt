package com.example.treasurehunter.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.treasurehunter.R

@Composable
fun CreateRoomScreen(navController: NavController) {
    var selectedPlayer by remember { mutableStateOf(0) }
    var selectedMap by remember { mutableStateOf(0) }

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
            PlayerSelection(selectedPlayer = selectedPlayer) { selectedPlayer = it }
            MapSelection(selectedMap = selectedMap) { selectedMap = it }
            Spacer(modifier = Modifier.height(24.dp))
            CreateButton(onClick = {
                // TODO: Add logic to create room
            })
        }
    }
}

@Composable
fun BackButton(onBackPress: () -> Unit) {
    IconButton(
        onClick = { onBackPress() },
        modifier = Modifier
            .padding(16.dp)
//            .align(Alignment.TopStart),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "Back",
            tint = Color.Black
        )
    }
}

@Composable
fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logotitle),
        contentDescription = "Logo",
        modifier = Modifier
            .size(120.dp)
            .padding(bottom = 16.dp)
    )
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
fun PlayerSelection(selectedPlayer: Int, onPlayerSelected: (Int) -> Unit) {
    Text(
        text = "Players",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf(5, 10, 15).forEachIndexed { index, number ->
            PlayerButton(
                number = number,
                isSelected = selectedPlayer == index,
                onClick = { onPlayerSelected(index) }
            )
        }
    }
}

@Composable
fun MapSelection(selectedMap: Int, onMapSelected: (Int) -> Unit) {
    Text(
        text = "Maps",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val maps = listOf("Hue", "Binh Phuoc", "TP.Ho Chi Minh")
        val drawableIds = listOf(R.drawable.hue, R.drawable.binhphuoc, R.drawable.tphochiminh)

        maps.forEachIndexed { index, text ->
            MapButton(
                text = text,
                isSelected = selectedMap == index,
                drawableId = drawableIds[index],
                onClick = { onMapSelected(index) }
            )
        }
    }
}

@Composable
fun PlayerButton(number: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .height(45.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (isSelected) Color(0xFFFF6D2E) else Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun MapButton(text: String, isSelected: Boolean, drawableId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFF6D2E) else Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = text,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xAA000000))
            )
        }
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun CreateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D2E)),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .width(150.dp)
            .height(50.dp)
    ) {
        Text(
            text = "Create",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
