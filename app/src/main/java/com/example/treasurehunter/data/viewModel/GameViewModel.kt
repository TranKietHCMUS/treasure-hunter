package com.example.treasurehunter.data.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.treasurehunter.data.model.ScreenMode
import com.example.treasurehunter.data.model.Treasure
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class GameViewModel @Inject constructor() : ViewModel()  {
    companion object {
        var screenMode by mutableStateOf(ScreenMode.MAP)

        fun changeScreenMode(mode: ScreenMode) {
            screenMode = mode
        }

        private var _gameLocation = mutableStateOf<LatLng?>(null)
        val gameLocation: LatLng? get() = _gameLocation.value

        private var _gameRadius = mutableStateOf(500.0)
        val gameRadius: Double get() = _gameRadius.value

        fun setGameLocation(location: LatLng) {
            _gameLocation.value = location
        }

        fun setGameRadius(radius: Double) {
            _gameRadius.value = radius
        }

        // Treasures
        private var _treasures = mutableStateOf<List<Treasure>>(emptyList())
        val treasures: List<Treasure> get() = _treasures.value

        // Tạo danh sách các kho báu từ tọa độ ngẫu nhiên
        fun generateTreasures(center: LatLng, radius: Double, count: Int = 9) {
            val treasureList = mutableListOf<Treasure>()
            for (i in 1..count) {
                val randomPoint = generateRandomLocation(center, radius)
                treasureList.add(Treasure(location = randomPoint))
            }
            _treasures.value = treasureList

            // Log tọa độ kho báu đã tạo
            treasureList.forEachIndexed { index, treasure ->
                Log.d("Treasure", "Treasure $index: (${treasure.location.latitude}, ${treasure.location.longitude})")
            }
        }

        // Đánh dấu kho báu đã tìm thấy
        fun markTreasureAsFound(location: LatLng) {
            _treasures.value = _treasures.value.map {
                if (it.location == location) it.copy(found = true) else it
            }
        }

        private fun generateRandomLocation(center: LatLng, radius: Double): LatLng {
            val radiusInDegrees = radius / 111000.0 // 111km ~ 1 độ
            val u = Math.random()
            val v = Math.random()
            val w = radiusInDegrees * Math.sqrt(u)
            val t = 2 * Math.PI * v
            val x = w * Math.cos(t)
            val y = w * Math.sin(t)

            val newLat = center.latitude + y
            val newLng = center.longitude + x / Math.cos(Math.toRadians(center.latitude))
            return LatLng(newLat, newLng)
        }
    }
}