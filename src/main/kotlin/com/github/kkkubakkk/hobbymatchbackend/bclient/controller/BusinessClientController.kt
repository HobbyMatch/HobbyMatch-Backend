package com.github.kkkubakkk.hobbymatchbackend.bclient.controller

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.UpdateClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils.Companion.getAuthenticatedUserId
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.CreateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/business")
class BusinessClientController(
    private val businessClientService: BusinessClientService,
    private val clientService: BusinessClientService,
) {
    private val logger = LoggerFactory.getLogger(BusinessClientController::class.java)

    @GetMapping("/{clientId}")
    fun getBusinessClient(
        @PathVariable clientId: Long,
    ): ResponseEntity<BusinessClientDTO> =
        try {
            ResponseEntity.ok(clientService.getClientById(clientId))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @GetMapping("/me")
    fun getMe(): ResponseEntity<BusinessClientDTO> {
        try {
            logger.info("Fetching business client information")
            val id = getAuthenticatedUserId()
            return ResponseEntity.ok(businessClientService.getMe(id).toDTO())
        } catch (e: Exception) {
            logger.error("Error fetching business client information", e)
            return ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{clientId}")
    fun updateBusinessClient(
        @PathVariable clientId: Long,
        @RequestBody updateClientDTO: UpdateClientDTO,
    ): ResponseEntity<BusinessClientDTO> =
        try {
            ResponseEntity.ok(clientService.updateClientById(clientId, updateClientDTO))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PostMapping("/venue/addVenue")
    fun addVenue(
        @RequestBody createVenueDTO: CreateVenueDTO,
    ): ResponseEntity<VenueDTO> =
        try {
            ResponseEntity.ok(clientService.addVenue(createVenueDTO))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @GetMapping("/venue/{venueId}")
    fun getVenue(
        @PathVariable venueId: Long,
    ): ResponseEntity<VenueDTO> =
        try {
            ResponseEntity.ok(clientService.getVenueById(venueId))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
}
