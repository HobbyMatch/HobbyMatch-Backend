package com.github.kkkubakkk.hobbymatchbackend.activity.controller

import com.github.kkkubakkk.hobbymatchbackend.activity.dto.ActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.CreateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.EnrollInActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.UpdateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.service.ActivityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/activities")
class ActivityController(
    private val activityService: ActivityService,
) {
    @GetMapping
    fun getAllActivities(): ResponseEntity<List<ActivityDTO>> = ResponseEntity.ok(activityService.getAllActivities())

    @PostMapping
    fun createActivity(
        @RequestBody createActivityDTO: CreateActivityDTO,
    ): ResponseEntity<ActivityDTO> =
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(activityService.createActivity(createActivityDTO))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PutMapping
    fun updateActivity(
        @RequestParam activityId: Long,
        @RequestBody updateActivityDTO: UpdateActivityDTO,
        @RequestParam organizerUsername: String,
    ): ResponseEntity<ActivityDTO> =
        try {
            ResponseEntity.ok(activityService.updateActivity(activityId, updateActivityDTO, organizerUsername))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @DeleteMapping
    fun deleteActivity(
        @RequestParam activityId: Long,
        @RequestParam organizerUsername: String,
    ): ResponseEntity<Unit> =
        try {
            activityService.deleteActivity(activityId, organizerUsername)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PostMapping("/enroll")
    fun enrollInActivity(
        @RequestParam activityId: Long,
        @RequestParam participantUsername: String,
    ): ResponseEntity<ActivityDTO> =
        try {
            val enrollDTO = EnrollInActivityDTO(participantUsername, activityId)
            ResponseEntity.ok(activityService.enrollInActivity(enrollDTO))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PostMapping("/withdraw")
    fun withdrawFromActivity(
        @RequestParam activityId: Long,
        @RequestParam participantUsername: String,
    ): ResponseEntity<ActivityDTO> =
        try {
            val enrollDTO = EnrollInActivityDTO(participantUsername, activityId)
            ResponseEntity.ok(activityService.withdrawFromActivity(enrollDTO))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
}
