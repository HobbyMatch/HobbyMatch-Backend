package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO

data class CreateUserDTO(
    val name: String,
    val email: String,
    val hobbies: List<HobbyDTO>,
)
