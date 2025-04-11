package com.github.kkkubakkk.hobbymatchbackend.location.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Location(
    @Column(name = "latitude", nullable = false)
    val latitude: Double,
    @Column(name = "longitude", nullable = false)
    val longitude: Double,
)
