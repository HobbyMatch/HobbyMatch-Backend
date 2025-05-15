package com.github.kkkubakkk.hobbymatchbackend.event.dto

import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserInEventDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toInEventDTO

data class EventInfoDTO(
    val id: Long,
    val title: String,
    val organizer: UserInEventDTO,
    val startTime: String,
)

fun Event.toInfoDTO() =
    EventInfoDTO(
        id = this.id,
        title = this.title,
        organizer = this.organizer.toInEventDTO(),
        startTime = this.startTime.toString(),
    )
