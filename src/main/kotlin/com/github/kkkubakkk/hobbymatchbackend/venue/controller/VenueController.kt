package com.github.kkkubakkk.hobbymatchbackend.venue.controller

import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils.Companion.getAuthenticatedUserId
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.CreateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.UpdateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.service.VenueService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/venues")
class VenueController(
    private val venueService: VenueService,
) {
    @PostMapping
    fun addVenue(
        @RequestBody createVenueDTO: CreateVenueDTO,
    ): ResponseEntity<VenueDTO> {
        val clientId = getAuthenticatedUserId()
        return ResponseEntity.ok(venueService.addVenue(clientId, createVenueDTO))
    }

    @GetMapping("/{venueId}")
    fun getVenue(
        @PathVariable venueId: Long,
    ): ResponseEntity<VenueDTO> = ResponseEntity.ok(venueService.getVenue(venueId).toDTO())

    @GetMapping
    fun getAllVenues(): ResponseEntity<List<VenueDTO>> = ResponseEntity.ok(venueService.getAllVenues())

    @DeleteMapping("/{venueId}")
    fun deleteVenue(
        @PathVariable venueId: Long,
    ) {
        val clientId = getAuthenticatedUserId()
        venueService.deleteVenue(venueId, clientId)
    }

    @PutMapping("/{venueId}")
    fun updateVenue(
        @PathVariable venueId: Long,
        @RequestBody updateVenueDTO: UpdateVenueDTO,
    ): ResponseEntity<VenueDTO> {
        val clientId = getAuthenticatedUserId()
        return ResponseEntity.ok(venueService.updateVenue(venueId, clientId, updateVenueDTO))
    }
}
