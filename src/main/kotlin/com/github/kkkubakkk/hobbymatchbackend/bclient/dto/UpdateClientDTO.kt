package com.github.kkkubakkk.hobbymatchbackend.bclient.dto

data class UpdateClientDTO(
    val name: String,
    val email: String,
    val taxId: String,
    // val venues: List<VenueDTO>,
)
