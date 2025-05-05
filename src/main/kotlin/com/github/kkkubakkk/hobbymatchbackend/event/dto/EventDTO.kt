package com.github.kkkubakkk.hobbymatchbackend.event.dto

import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserInEventDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toInEventDTO

data class EventDTO(
    val id: Long,
    val organizer: UserInEventDTO,
    val participants: List<UserInEventDTO>,
    val title: String,
    val description: String?,
    // val location: LocationDTO,
    val location: Location,
    val startTime: String,
    val endTime: String,
    val hobbies: List<HobbyDTO>,
    val price: Double,
    val minUsers: Int,
    val maxUsers: Int,
)

fun Event.toDTO(): EventDTO =
    EventDTO(
        id = this.id,
        organizer = this.organizer.toInEventDTO(),
        participants = this.participants.map { it.toInEventDTO() },
        title = this.title,
        description = this.description,
        location = this.location,
        startTime = this.startTime.toString(),
        endTime = this.startTime.toString(),
        hobbies = this.hobbies.map { it.toDTO() },
        price = this.price,
        minUsers = this.minUsers,
        maxUsers = this.maxUsers,
    )
