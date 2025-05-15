package com.github.kkkubakkk.hobbymatchbackend.event.controller

import com.github.kkkubakkk.hobbymatchbackend.event.dto.CreateOrUpdateEventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.EventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.event.service.EventService
import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils.Companion.getAuthenticatedUserId
import org.springframework.http.HttpStatus
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
@RequestMapping("/api/v1/events")
class EventController(
    private val eventService: EventService,
) {
    @GetMapping
    fun getAllEvents(): ResponseEntity<List<EventDTO>> = ResponseEntity.ok(eventService.getAllEvents())

    @GetMapping("/{eventId}")
    fun getEvent(
        @PathVariable eventId: Long,
    ): ResponseEntity<EventDTO> = ResponseEntity.ok(eventService.getEvent(eventId).toDTO())

    @PostMapping
    fun createEvent(
        @RequestBody createOrUpdateEventDTO: CreateOrUpdateEventDTO,
    ): ResponseEntity<EventDTO> {
        val authUserId = getAuthenticatedUserId()
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(eventService.createEvent(createOrUpdateEventDTO, authUserId))
    }

    @PutMapping("/{eventId}")
    fun updateEvent(
        @PathVariable eventId: Long,
        @RequestBody createOrUpdateEventDTO: CreateOrUpdateEventDTO,
    ): ResponseEntity<EventDTO> {
        val authUserId = getAuthenticatedUserId()
        return ResponseEntity.ok(eventService.updateEvent(eventId, authUserId, createOrUpdateEventDTO))
    }

    @DeleteMapping("/{eventId}")
    fun deleteActivity(
        @PathVariable eventId: Long,
    ) {
        val authUserId = getAuthenticatedUserId()
        eventService.deleteEvent(eventId, authUserId)
    }

    @PostMapping("/{eventId}/enroll")
    fun enrollInEvent(
        @PathVariable eventId: Long,
    ): ResponseEntity<EventDTO> {
        val authUserId = getAuthenticatedUserId()
        return ResponseEntity.ok(eventService.enrollInEvent(eventId, authUserId))
    }

    @PostMapping("/{eventId}/withdraw")
    fun withdrawFromEvent(
        @PathVariable eventId: Long,
    ): ResponseEntity<EventDTO> {
        val authUserId = getAuthenticatedUserId()
        return ResponseEntity.ok(eventService.withdrawFromEvent(eventId, authUserId))
    }
}
