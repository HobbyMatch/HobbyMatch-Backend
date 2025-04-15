package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.user.model.User

data class UserInfoDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val birthday: String,
)

fun User.toInfoDTO(): UserInfoDTO =
    UserInfoDTO(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        email = this.email,
        birthday = this.birthday.toString(),
    )
