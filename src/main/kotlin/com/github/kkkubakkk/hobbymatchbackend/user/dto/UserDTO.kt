package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User

data class UserDTO(
    val id: Long,
    val name: String,
    val email: String,
    val hobbies: List<HobbyDTO>,
)

fun User.toDTO(): UserDTO =
    UserDTO(
        id = this.id,
        name = this.name,
        email = this.email,
        hobbies = this.hobbies.map { it.toDTO() },
    )
