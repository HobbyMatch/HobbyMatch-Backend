package com.github.kkkubakkk.hobbymatchbackend.hobby.dto

import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby

data class HobbyDTO(
    val name: String,
)

fun Hobby.toDTO(): HobbyDTO =
    HobbyDTO(
        name = this.name,
    )
