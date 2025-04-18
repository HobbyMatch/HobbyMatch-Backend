package com.github.kkkubakkk.hobbymatchbackend.activity.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class UpdateActivityDTO(
    val maxParticipants: Int,
    val title: String,
    val description: String?,
    val location: Location,
    val datetime: String,
    val hobby: HobbyDTO,
)
