package com.example.treasurehunter.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.treasurehunter.R

@Composable
fun CreateRoomScreen(navController: NavController) {
    var selectedRadius by remember { mutableStateOf(0) } // Lưu lựa chọn bán kính

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
            RadiusSelection(selectedRadius = selectedRadius) { selectedRadius = it } // Chọn bán kính
            Spacer(modifier = Modifier.height(24.dp))
            CreateButton(onClick = {
                // TODO: Thêm logic tạo phòng với bán kính đã chọn
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
            .size(250.dp)
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
fun RadiusSelection(selectedRadius: Int, onRadiusSelected: (Int) -> Unit) {
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
        val radii = listOf("1km", "2km", "5km") // Danh sách bán kính

        radii.forEachIndexed { index, radius ->
            RadiusButton(
                text = radius,
                isSelected = selectedRadius == index,
                onClick = { onRadiusSelected(index) }
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
