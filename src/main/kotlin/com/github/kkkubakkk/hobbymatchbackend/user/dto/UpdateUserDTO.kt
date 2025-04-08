package com.github.kkkubakkk.hobbymatchbackend.user.dto

data class UpdateUserDTO(
    val firstName: String,
    val lastName: String,
    val username: String,
    val hobbiesId: List<Long>,
    val bio: String?,
)
