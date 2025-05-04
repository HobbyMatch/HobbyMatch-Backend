package com.github.kkkubakkk.hobbymatchbackend.venue.repository

import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface VenueRepository : JpaRepository<Venue, Long> {
    fun findByLocation(location: Location): Optional<Venue>
}
