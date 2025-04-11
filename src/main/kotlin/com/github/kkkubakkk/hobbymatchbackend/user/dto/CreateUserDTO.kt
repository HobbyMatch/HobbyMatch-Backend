package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO

data class CreateUserDTO(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val birthday: String,
    val bio: String?,
    val hobbies: List<HobbyDTO>,
)
