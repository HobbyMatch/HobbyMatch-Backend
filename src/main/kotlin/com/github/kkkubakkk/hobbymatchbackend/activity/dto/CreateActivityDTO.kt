package com.github.kkkubakkk.hobbymatchbackend.activity.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class CreateActivityDTO(
    val organizerUsername: String,
    val title: String,
    val description: String,
//    val longitude: Double,
//    val latitude: Double,
    val datetime: String,
    val location: Location,
    val hobbies: List<HobbyDTO>,
)
