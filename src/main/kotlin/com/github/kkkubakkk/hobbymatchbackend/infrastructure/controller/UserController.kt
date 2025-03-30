package com.github.kkkubakkk.hobbymatchbackend.infrastructure.controller
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserController(private val userService: UserService) {
    @GetMapping("/users")
    fun getAllUsers():List<UserDTO> {
        return userService.getAllUsers()
    }
    @PostMapping("/users"): UserDTO{
        return userService.createUser();
    }
    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id:Long): UserDTO {
        return userService.getUserById(id)
    }
    @PutMapping("/users/{id}")
    fun updateUser(@PathVariable id:Long,
                   @RequestBody userDTO: UserDTO): UserDTO{
        return userService.updateUser(id, userDTO)
    }
    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id:Long){ userService.deleteUser(id)}


}