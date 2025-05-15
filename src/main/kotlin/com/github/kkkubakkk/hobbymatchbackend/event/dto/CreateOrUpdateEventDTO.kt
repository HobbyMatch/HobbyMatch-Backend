package com.github.kkkubakkk.hobbymatchbackend.event.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.location.dto.LocationDTO

data class CreateOrUpdateEventDTO(
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val location: LocationDTO,
    val price: Double,
    val minUsers: Int,
    val maxUsers: Int,
    val hobbies: List<HobbyDTO>,
)
