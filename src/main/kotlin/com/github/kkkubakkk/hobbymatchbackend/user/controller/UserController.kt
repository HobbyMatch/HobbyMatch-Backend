package com.github.kkkubakkk.hobbymatchbackend.user.controller

import com.github.kkkubakkk.hobbymatchbackend.user.dto.CreateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.EmailUpdateDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.SearchUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
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
@RequestMapping("/api")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/users")
    fun getAllUsers(): List<UserDTO> = userService.getAllUsers()

    @PostMapping("/users")
    fun createUser(
        @RequestBody createUserDTO: CreateUserDTO,
    ): UserDTO = userService.createUser(createUserDTO)

    @GetMapping("/users/{id}")
    fun getUserById(
        @PathVariable id: Long,
    ): UserDTO = userService.getUserById(id)

    @GetMapping("/users/email/{email}")
    fun getUserByEmail(
        @PathVariable email: String,
    ): UserDTO = userService.getUserByEmail(email)

    @GetMapping("/users/username/{username}")
    fun getUserByUsername(
        @PathVariable username: String,
    ): UserDTO = userService.getUserByUsername(username)

    @GetMapping("/users/exists/{email}")
    fun userExists(
        @PathVariable email: String,
    ): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.userExists(email)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody updateUserDTO: UpdateUserDTO,
    ): UserDTO = userService.updateUser(id, updateUserDTO)

    @PutMapping("/users/email/{email}")
    fun updateUserByEmail(
        @PathVariable email: String,
        @RequestBody userDTO: UserDTO,
    ): UserDTO = userService.updateUserByEmail(email, userDTO)

    @PutMapping("/users/{id}/email")
    fun updateUserEmail(
        @PathVariable id: Long,
        @RequestBody emailUpdateDTO: EmailUpdateDTO,
    ): UserDTO = userService.updateUserEmail(id, emailUpdateDTO)

    @DeleteMapping("/users/{id}")
    fun deleteUser(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/users/search")
    fun searchUsers(
        @RequestBody searchUserDTO: SearchUserDTO,
    ): List<UserDTO> = userService.searchUsers(searchUserDTO)
}
