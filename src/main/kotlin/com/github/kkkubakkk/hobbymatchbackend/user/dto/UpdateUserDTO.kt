package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO

data class UpdateUserDTO(
    val name: String,
    val email: String,
    val hobbies: List<HobbyDTO>,
)
