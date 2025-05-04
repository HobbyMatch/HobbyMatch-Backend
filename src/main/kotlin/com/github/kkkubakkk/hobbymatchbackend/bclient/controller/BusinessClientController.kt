package com.github.kkkubakkk.hobbymatchbackend.bclient.controller

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.UpdateClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1")
@RestController
class BusinessClientController(
    private val clientService: BusinessClientService,
) {
    @GetMapping("/business/{clientId}")
    fun getBusinessClient(
        @PathVariable clientId: Long,
    ): ResponseEntity<BusinessClientDTO> =
        try {
            ResponseEntity.ok(clientService.getClientById(clientId))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PutMapping("/business/{clientId}")
    fun updateBusinessClient(
        @PathVariable clientId: Long,
        @RequestBody updateClientDTO: UpdateClientDTO,
    ): ResponseEntity<BusinessClientDTO> =
        try {
            ResponseEntity.ok(clientService.updateClientById(clientId, updateClientDTO))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @GetMapping("business/venue/{venueId}")
    fun getVenue(
        @PathVariable venueId: Long,
    ): ResponseEntity<VenueDTO> =
        try {
            ResponseEntity.ok(clientService.getVenueById(venueId))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
}
