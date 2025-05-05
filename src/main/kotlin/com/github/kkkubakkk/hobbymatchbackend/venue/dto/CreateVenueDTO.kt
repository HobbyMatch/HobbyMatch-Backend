package com.github.kkkubakkk.hobbymatchbackend.venue.dto

import com.github.kkkubakkk.hobbymatchbackend.activity.dto.ActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class CreateVenueDTO(
    val location: Location,
    val hostedActivities: List<ActivityDTO>,
    val ownerId: Long,
    // val owner: BusinessClientDTO,
)
