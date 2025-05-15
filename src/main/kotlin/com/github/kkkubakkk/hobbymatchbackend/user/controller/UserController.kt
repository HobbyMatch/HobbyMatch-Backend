package com.github.kkkubakkk.hobbymatchbackend.user.controller

import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils.Companion.getAuthenticatedUserId
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: Long,
    ): ResponseEntity<UserDTO> {
        val authUserId = getAuthenticatedUserId()
        userService.verifyUserAccess(userId, authUserId)
        return ResponseEntity.ok(userService.getUser(userId).toDTO())
//        try {
//            logger.info("Fetching user with ID: $userId")
//
//            val authUserId = getAuthenticatedUserId()
//            if (authUserId != userId) {
//                logger.warn("User ID mismatch: Authenticated user ID: $authUserId, Requested user ID: $userId")
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
//            }
//
//            val user = userService.getUser(userId)
//            return ResponseEntity.ok(user.toDTO())
//        } catch (e: Exception) {
//            logger.error("Error fetching user with ID: $userId", e)
//            return ResponseEntity.notFound().build()
//        }
    }

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: Long,
        @RequestBody userDto: UpdateUserDTO,
    ): ResponseEntity<UserDTO> {
        val authUserId = getAuthenticatedUserId()
        userService.verifyUserAccess(userId, authUserId)
        return ResponseEntity.ok(userService.updateUser(userId, userDto).toDTO())
//        try {
//            logger.info("Updating user with ID: $userId")
//
//            val authUserId = getAuthenticatedUserId()
//            if (authUserId != userId) {
//                logger.warn("User ID mismatch: Authenticated user ID: $authUserId, Requested user ID: $userId")
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
//            }
//
//            val updatedUser = userService.updateUser(userId, userDto)
//            return ResponseEntity.ok(updatedUser.toDTO())
//        } catch (e: Exception) {
//            logger.error("Error updating user with ID: $userId", e)
//            return ResponseEntity.notFound().build()
//        }
    }
}
