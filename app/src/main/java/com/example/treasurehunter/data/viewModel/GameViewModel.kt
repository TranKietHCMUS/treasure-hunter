package com.example.treasurehunter.data.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.treasurehunter.data.model.ScreenMode
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
    }
}