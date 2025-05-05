package com.github.kkkubakkk.hobbymatchbackend.bclient.dto

import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO

data class BusinessClientDTO(
    val id: Long,
    val name: String,
    val email: String,
    val venues: List<VenueDTO>,
)
