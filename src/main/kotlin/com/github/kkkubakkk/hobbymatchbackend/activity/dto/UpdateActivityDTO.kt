package com.github.kkkubakkk.hobbymatchbackend.activity.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class UpdateActivityDTO(
    val title: String,
    val description: String?,
//    val longitude: Double,
//    val latitude: Double,
    val location: Location,
    val datetime: String,
    val hobbies: List<HobbyDTO>,
)
