package com.github.kkkubakkk.hobbymatchbackend.activity.controller

import com.github.kkkubakkk.hobbymatchbackend.activity.dto.ActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.CreateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.EnrollInActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.UpdateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.WithdrawFromActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.service.ActivityService
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(ActivityController::class.java)

    @GetMapping
    fun getAllActivities(): ResponseEntity<List<ActivityDTO>> = ResponseEntity.ok(activityService.getAllActivities())

    @PostMapping
    fun createActivity(
        @RequestBody createActivityDTO: CreateActivityDTO,
    ): ResponseEntity<Any> =
        try {
            ResponseEntity.status(HttpStatus.CREATED).body(activityService.createActivity(createActivityDTO))
        } catch (e: IllegalArgumentException) {
            logger.error("Failed to create activity: ${e.message}", e)
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to e.message))
        }

    @PutMapping
    fun updateActivity(
        @RequestParam activityId: Long,
        @RequestBody updateActivityDTO: UpdateActivityDTO,
        @RequestParam organizerUsername: String,
    ): ResponseEntity<Any> =
        try {
            ResponseEntity.ok(activityService.updateActivity(activityId, updateActivityDTO, organizerUsername))
        } catch (e: IllegalArgumentException) {
            logger.error("Failed to update activity: ${e.message}", e)
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to e.message))
        }

    @DeleteMapping
    fun deleteActivity(
        @RequestParam activityId: Long,
        @RequestParam organizerUsername: String,
    ): ResponseEntity<Any> =
        try {
            activityService.deleteActivity(activityId, organizerUsername)
            ResponseEntity.ok().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Failed to delete activity: ${e.message}", e)
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to e.message))
        }

    @PostMapping("/enroll")
    fun enrollInActivity(
        @RequestBody enrollDTO: EnrollInActivityDTO,
    ): ResponseEntity<Any> =
        try {
            ResponseEntity.ok(activityService.enrollInActivity(enrollDTO))
        } catch (e: IllegalArgumentException) {
            logger.error("Failed to enroll in activity: ${e.message}", e)
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to e.message))
        }

    @PostMapping("/withdraw")
    fun withdrawFromActivity(
        @RequestBody withdrawDTO: WithdrawFromActivityDTO,
    ): ResponseEntity<Any> =
        try {
            ResponseEntity.ok(activityService.withdrawFromActivity(withdrawDTO))
        } catch (e: IllegalArgumentException) {
            logger.error("Failed to withdraw from activity: ${e.message}", e)
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to e.message))
        }
}
