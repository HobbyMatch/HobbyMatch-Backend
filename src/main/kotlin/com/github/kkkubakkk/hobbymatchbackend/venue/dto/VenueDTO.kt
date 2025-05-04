package com.github.kkkubakkk.hobbymatchbackend.venue.dto

import com.github.kkkubakkk.hobbymatchbackend.activity.dto.ActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location

data class VenueDTO(
    val id: Long,
    val location: Location,
    val hostedActivities: List<ActivityDTO>,
    val owner: BusinessClientDTO,
)
