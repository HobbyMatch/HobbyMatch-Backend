package com.github.kkkubakkk.hobbymatchbackend.activity.dto

import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO

data class ActivityDTO(
    val id: Long,
    val organizer: UserDTO,
    val participants: List<UserDTO>,
    val title: String,
    val description: String?,
//    val longitude: Double,
//    val latitude: Double,
    val location: Location,
    val datetime: String,
    val hobbies: List<HobbyDTO>,
)

fun Activity.toDTO(): ActivityDTO =
    ActivityDTO(
        id = this.id,
        organizer = this.organizer.toDTO(),
        participants = this.participants.map { it.toDTO() },
        title = this.title,
        description = this.description,
//        longitude = this.location.longitude,
//        latitude = this.location.latitude,
        location = this.location,
        datetime = this.dateTime.toString(),
        hobbies = this.hobbies.map { it.toDTO() },
    )
