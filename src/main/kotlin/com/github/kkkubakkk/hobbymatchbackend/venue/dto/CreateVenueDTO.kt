package com.github.kkkubakkk.hobbymatchbackend.venue.dto

import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class CreateVenueDTO(
    val name: String,
    val description: String,
    val address: String,
    val location: Location,
)
