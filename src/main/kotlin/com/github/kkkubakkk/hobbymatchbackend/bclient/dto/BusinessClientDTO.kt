package com.github.kkkubakkk.hobbymatchbackend.bclient.dto

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO

data class BusinessClientDTO (
    val id: Long,
    val name: String,
    val email: String,
    val venues: List<VenueDTO>,
    ) {
    fun BusinessClient.toDTO(): BusinessClientDTO =
        BusinessClientDTO(
        id = this.id,
        name = this.name,
        email = this.email,
        venues = this.venues.map { it.toDTO() },
    )
}