package com.github.kkkubakkk.hobbymatchbackend.venue.dto

import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue

data class VenueInfoDTO(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val location: Location,
)

fun Venue.toInfoDTO(): VenueInfoDTO =
    VenueInfoDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        address = this.address,
        location = this.location,
    )
