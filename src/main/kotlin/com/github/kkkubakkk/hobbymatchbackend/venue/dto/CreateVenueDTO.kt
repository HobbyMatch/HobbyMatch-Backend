package com.github.kkkubakkk.hobbymatchbackend.venue.dto

import com.github.kkkubakkk.hobbymatchbackend.event.dto.EventDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class CreateVenueDTO(
    val location: Location,
    val hostedActivities: List<EventDTO> = emptyList(),
    val ownerId: Long,
    // val owner: BusinessClientDTO,
)
