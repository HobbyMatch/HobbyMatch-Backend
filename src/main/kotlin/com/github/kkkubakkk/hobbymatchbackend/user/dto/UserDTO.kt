package com.github.kkkubakkk.hobbymatchbackend.user.dto

import com.github.kkkubakkk.hobbymatchbackend.user.model.User

data class UserDTO(
    val id:Long,
    val firstName:String,
    val lastName:String,
    val username: String,
    val email: String
    )
fun User.toDTO():UserDTO = UserDTO(
    id=this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    username = this.username,
    email = this.email
    )
fun UserDTO.toEntity(): User = User(id = this.id,
    firstName = this.firstName,
    lastName=this.lastName,
    username=this.username,
    email=this.email)
