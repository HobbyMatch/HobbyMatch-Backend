package com.github.kkkubakkk.hobbymatchbackend.activity.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO

data class CreateActivityDTO(
    val organizerUsername: String,
    val title: String,
    val description: String,
    val longitude: Double,
    val latitude: Double,
    val datetime: String,
    val hobbies: List<HobbyDTO>,
)
