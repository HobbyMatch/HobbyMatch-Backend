package com.github.kkkubakkk.hobbymatchbackend.location.dto

import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class LocationDTO(
    val longitude: Double,
    val latitude: Double,
)

fun Location.toDTO(): LocationDTO = LocationDTO(longitude, latitude)
