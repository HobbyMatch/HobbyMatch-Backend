package com.github.kkkubakkk.hobbymatchbackend.event.controller

import com.github.kkkubakkk.hobbymatchbackend.event.dto.CreateOrUpdateEventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.EventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.service.EventService
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
    ): ResponseEntity<EventDTO> =
        try {
            ResponseEntity.ok(eventService.getEvent(eventId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PostMapping
    fun createEvent(
        @RequestBody createOrUpdateEventDTO: CreateOrUpdateEventDTO,
    ): ResponseEntity<EventDTO> =
        try {
            val authUserId = getAuthenticatedUserId()
            ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(createOrUpdateEventDTO, authUserId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PutMapping("/{eventId}")
    fun updateEvent(
        @PathVariable eventId: Long,
        @RequestBody createOrUpdateEventDTO: CreateOrUpdateEventDTO,
    ): ResponseEntity<EventDTO> {
        try {
            val authUserId = getAuthenticatedUserId()
            if (authUserId != eventService.getEvent(eventId).id) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }
            return ResponseEntity.ok(eventService.updateEvent(eventId, createOrUpdateEventDTO))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/{eventId}")
    fun deleteActivity(
        @PathVariable eventId: Long,
    ): ResponseEntity<Unit> {
        try {
            val authUserId = getAuthenticatedUserId()
            if (authUserId != eventService.getEvent(eventId).id) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }
            eventService.deleteEvent(eventId)
            return ResponseEntity.status(HttpStatus.OK).build()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/{eventId}/enroll")
    fun enrollInEvent(
        @PathVariable eventId: Long,
    ): ResponseEntity<EventDTO> =
        try {
            val authUserId = getAuthenticatedUserId()
            ResponseEntity.ok(eventService.enrollInEvent(eventId, authUserId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PostMapping("/{eventId}/withdraw")
    fun withdrawFromEvent(
        @PathVariable eventId: Long,
    ): ResponseEntity<EventDTO> =
        try {
            val authUserId = getAuthenticatedUserId()
            ResponseEntity.ok(eventService.withdrawFromEvent(eventId, authUserId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
}
