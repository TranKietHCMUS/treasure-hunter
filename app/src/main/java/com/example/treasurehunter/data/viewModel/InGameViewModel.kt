package com.example.treasurehunter.data.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.treasurehunter.data.model.ScreenMode
import javax.inject.Inject

class InGameViewModel @Inject constructor() : ViewModel()  {
    companion object {
        var screenMode by mutableStateOf(ScreenMode.MAP)

        fun changeScreenMode(mode: ScreenMode) {
            screenMode = mode
        }
    }
}