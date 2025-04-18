package com.github.kkkubakkk.hobbymatchbackend.activity.dto

import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserInfoDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toInfoDTO

// TODO: Create a new endpoint to get all participants of the activity
// TODO: Update OpenApi specification
data class ActivityDTO(
    val id: Long,
    val organizer: UserInfoDTO,
    val participants: Int,
    val maxParticipants: Int,
    val title: String,
    val description: String?,
    val location: Location,
    val datetime: String,
    val hobby: HobbyDTO,
)

fun Activity.toDTO(): ActivityDTO =
    ActivityDTO(
        id = this.id,
        organizer = this.organizer.toInfoDTO(),
        participants = this.participants.size,
        maxParticipants = this.maxParticipants,
        title = this.title,
        description = this.description,
        location = this.location,
        datetime = this.dateTime.toString(),
        hobby = this.hobby.toDTO(),
    )
