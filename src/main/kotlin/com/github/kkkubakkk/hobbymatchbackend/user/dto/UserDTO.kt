package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User

data class UserDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val hobbies: List<HobbyDTO>,
    val birthday: String,
    val bio: String?,
)

fun User.toDTO(): UserDTO =
    UserDTO(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        email = this.email,
        hobbies = this.hobbies.map { it.toDTO() },
        birthday = this.birthday.toString(),
        bio = this.bio,
    )
