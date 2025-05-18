package com.github.kkkubakkk.hobbymatchbackend.bclient.dto

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueInfoDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.toInfoDTO

data class BusinessClientDTO(
    val id: Long,
    val name: String,
    val email: String,
    val taxId: String,
    val venues: List<VenueInfoDTO>,
)

fun BusinessClient.toDTO(): BusinessClientDTO =
    BusinessClientDTO(
        id = this.id,
        name = this.name,
        email = this.email,
        taxId = this.taxId,
        venues = this.venues.map { it.toInfoDTO() },
    )
