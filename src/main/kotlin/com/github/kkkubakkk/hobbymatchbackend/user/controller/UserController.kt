package com.github.kkkubakkk.hobbymatchbackend.user.controller

import com.github.kkkubakkk.hobbymatchbackend.user.dto.CreateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.SearchUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
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
@RequestMapping("/api")
class UserController(
    private val userService: UserService,
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/users")
    fun getAllUsers(): List<UserDTO> = userService.getAllUsers()

    @PostMapping("/users")
    fun createUser(
        @RequestBody createUserDTO: CreateUserDTO,
    ): ResponseEntity<UserDTO> =
        try {
            ResponseEntity.ok(userService.createUser(createUserDTO))
        } catch (ex: IllegalArgumentException) {
            logger.error("Failed to create user", ex)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @GetMapping("/users/email/{email}")
    fun getUserByEmail(
        @PathVariable email: String,
    ): ResponseEntity<UserDTO> =
        try {
            ResponseEntity.ok(userService.getUserByEmail(email))
        } catch (ex: IllegalArgumentException) {
            logger.error("Failed to find user with email: $email", ex)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @GetMapping("/users/username/{username}")
    fun getUserByUsername(
        @PathVariable username: String,
    ): ResponseEntity<UserDTO> =
        try {
            ResponseEntity.ok(userService.getUserByUsername(username))
        } catch (ex: IllegalArgumentException) {
            logger.error("Failed to find user with username: $username", ex)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @GetMapping("/users/exists/{email}")
    fun userExists(
        @PathVariable email: String,
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.userExists(email)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @PutMapping("/users/email/{email}")
    fun updateUser(
        @PathVariable email: String,
        @RequestBody updateUserDTO: UpdateUserDTO,
    ): ResponseEntity<UserDTO> =
        try {
            ResponseEntity.ok(userService.updateUserByEmail(email, updateUserDTO))
        } catch (ex: IllegalArgumentException) {
            logger.error("Failed to update user with email: $email", ex)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PutMapping("/users/username/{username}")
    fun updateUserByUsername(
        @PathVariable username: String,
        @RequestBody updateUserDTO: UpdateUserDTO,
    ): ResponseEntity<UserDTO> =
        try {
            ResponseEntity.ok(userService.updateUserByUsername(username, updateUserDTO))
        } catch (ex: IllegalArgumentException) {
            logger.error("Failed to update user with username: $username", ex)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PostMapping("/users/search")
    fun searchUsers(
        @RequestBody searchUserDTO: SearchUserDTO,
    ): List<UserDTO> = userService.searchUsers(searchUserDTO)

    @PutMapping("/users/email/{email}/activate")
    fun activateUser(
        @PathVariable email: String,
    ): ResponseEntity<UserDTO> =
        try {
            ResponseEntity.ok(userService.activateUser(email))
        } catch (ex: IllegalArgumentException) {
            logger.error("Failed to activate user with email: $email", ex)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

    @PutMapping("/users/email/{email}/deactivate")
    fun deactivateUser(
        @PathVariable email: String,
    ): ResponseEntity<UserDTO> =
        try {
            ResponseEntity.ok(userService.deactivateUser(email))
        } catch (ex: IllegalArgumentException) {
            logger.error("Failed to deactivate user with email: $email", ex)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
}
