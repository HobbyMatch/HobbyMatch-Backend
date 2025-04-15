package com.github.kkkubakkk.hobbymatchbackend.activity.dto

import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserInfoDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toInfoDTO

// TODO: Add number od participants do the dto
// TODO: Add max number of participants to the dto and the model
// TODO: Create a new endpoint to get all participants of the activity
// TODO: Change activity model to use just one hobby (seems reasonable)
// TODO: Update OpenaApi specification
data class ActivityDTO(
    val id: Long,
    val organizer: UserInfoDTO,
//    val participants: List<UserInfoDTO>,
    val title: String,
    val description: String?,
    val longitude: Double,
    val latitude: Double,
    val datetime: String,
//    val hobbies: List<HobbyDTO>,
)

fun Activity.toDTO(): ActivityDTO =
    ActivityDTO(
        id = this.id,
        organizer = this.organizer.toInfoDTO(),
//        participants = this.participants.map { it.toInfoDTO() },
        title = this.title,
        description = this.description,
        longitude = this.location.longitude,
        latitude = this.location.latitude,
        datetime = this.dateTime.toString(),
//        hobbies = this.hobbies.map { it.toDTO() },
    )
