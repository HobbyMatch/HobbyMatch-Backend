package com.github.kkkubakkk.hobbymatchbackend.bclient.dto

import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueInfoDTO

data class BusinessClientDTO(
    val id: Long,
    val name: String,
    val email: String,
    val venues: List<VenueInfoDTO>,
)

// fun BusinessClient.toDTO(): BusinessClientDTO =
//    BusinessClientDTO(
//        id = this.id,
//        name = this.name,
//        email = this.email,
//        venues = this.venues.map { it.toInfoDTO() },
//    )
