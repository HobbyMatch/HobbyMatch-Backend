package com.github.kkkubakkk.hobbymatchbackend.security.dto

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.user.model.User

data class LoginDTO(
    val id: Long = 0,
    val email: String = "",
    val name: String = "",
)

fun User.toLoginDTO(): LoginDTO =
    LoginDTO(
        id = this.id,
        email = this.email,
        name = this.name,
    )

fun BusinessClient.toLoginDTO(): LoginDTO =
    LoginDTO(
        id = this.id,
        email = this.email,
        name = this.name,
    )
