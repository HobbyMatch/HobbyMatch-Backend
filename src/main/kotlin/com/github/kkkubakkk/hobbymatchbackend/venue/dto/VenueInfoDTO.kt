package com.github.kkkubakkk.hobbymatchbackend.venue.dto

import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue

data class VenueInfoDTO(
    val id: Long,
    val location: Location,
)

fun Venue.toInfoDTO(): VenueInfoDTO =
    VenueInfoDTO(
        id = this.id,
        location = this.location,
    )
