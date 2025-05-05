package com.github.kkkubakkk.hobbymatchbackend.event.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class CreateOrUpdateEventDTO(
    val title: String,
    val description: String,
    val location: Location,
    val startTime: String,
    val endTime: String,
    val hobbies: List<HobbyDTO>,
    val price: Double,
    val minUsers: Int,
    val maxUsers: Int,
)
