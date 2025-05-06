package com.github.kkkubakkk.hobbymatchbackend.venue.dto

import com.github.kkkubakkk.hobbymatchbackend.event.dto.EventDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class VenueDTO(
    val id: Long,
    val name: String,
    val location: Location,
    val hostedActivities: List<EventDTO>,
    val ownerId: Long,
    // val owner: BusinessClientDTO,
)
