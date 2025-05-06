package com.github.kkkubakkk.hobbymatchbackend.bclient.dto

import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO

data class BusinessClientDTO(
    val id: Long,
    val name: String,
    val email: String,
    val venues: List<VenueDTO>,
)

// fun BusinessClient.toDTO(): BusinessClientDTO =
//    BusinessClientDTO(
//        id = this.id,
//        name = this.name,
//        email = this.email,
//        venues = this.venues.map { it.toInfoDTO() },
//    )
