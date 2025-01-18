package com.example.treasurehunter.data.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TreasureViewModel @Inject constructor() : ViewModel() {
    companion object {
        var isChestShow by mutableStateOf(false)

        fun showChest() {
            Log.i("MainActivity", "Set chest show")
            isChestShow = true
        }

        fun hideChest() {
            isChestShow = false
        }
        @JvmStatic
        fun openChest() {
            showChest()
            PuzzleViewModel.showPiece()
            ScoreViewModel.increaseScore()
        }
    }
}