package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO

data class UpdateUserDTO(
    val firstName: String,
    val lastName: String,
    val username: String,
    val hobbies: List<HobbyDTO>,
    val bio: String?,
)
