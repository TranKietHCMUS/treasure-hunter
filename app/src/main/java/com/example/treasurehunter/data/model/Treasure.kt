package com.example.treasurehunter.data.model

import com.google.android.gms.maps.model.LatLng

data class Treasure(
    val location: LatLng,
    var found: Boolean = false
)