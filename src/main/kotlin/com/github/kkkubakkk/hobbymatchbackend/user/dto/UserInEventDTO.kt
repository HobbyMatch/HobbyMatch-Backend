package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.user.model.User

data class UserInEventDTO(
    val id: Long,
    val name: String,
)

fun User.toInEventDTO(): UserInEventDTO =
    UserInEventDTO(
        id = this.id,
        name = this.name,
    )
