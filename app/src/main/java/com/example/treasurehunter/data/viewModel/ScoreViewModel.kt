package com.example.treasurehunter.data.viewModel

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ScoreViewModel @Inject constructor() : ViewModel() {
    companion object {
        var score = 0;

        fun increaseScore() {
            score += 1;
            if (PuzzleViewModel.isFinished()) {
                score += 1;
            }
        }
    }
}