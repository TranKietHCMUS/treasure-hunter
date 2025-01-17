package com.example.treasurehunter.data.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class LocationViewModel @Inject constructor() : ViewModel() {
    companion object {
        var _listCoordinates = mutableStateOf<List<Pair<Double, Double>>>(
            listOf(
                Pair(10.7481774, 106.704646),
                Pair(10.748627060802958, 106.704646),
                Pair(10.748495358203005, 106.70496963596538),
                Pair(10.7481774, 106.70510369037152),
                Pair(10.747859441796994, 106.70496963596538),
                Pair(10.74772773919704, 106.704646),
                Pair(10.747859441796994, 106.70432236403461),
                Pair(10.7481774, 106.70418830962848),
                Pair(10.748495358203005, 106.70432236403461)
            )
        )
        val listCoordinates: List<Pair<Double, Double>> get() = _listCoordinates.value

        fun setListCoordinates(coordinates: List<Pair<Double, Double>>) {
            _listCoordinates.value = coordinates
        }
    }
}