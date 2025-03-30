package com.github.kkkubakkk.hobbymatchbackend.user.dto

data class CreateUserDTO(
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
)
