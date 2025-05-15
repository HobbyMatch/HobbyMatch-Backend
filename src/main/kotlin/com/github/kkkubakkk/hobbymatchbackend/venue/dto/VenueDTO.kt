package com.github.kkkubakkk.hobbymatchbackend.venue.dto

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.ClientInfoDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.toInfoDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.EventInfoDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.toInfoDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue

data class VenueDTO(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val location: Location,
    val owner: ClientInfoDTO,
    val events: List<EventInfoDTO>,
)

fun Venue.toDTO() =
    VenueDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        address = this.address,
        location = this.location,
        owner = this.owner.toInfoDTO(),
        events = this.events.map { it.toInfoDTO() },
    )
