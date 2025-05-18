package com.github.kkkubakkk.hobbymatchbackend.bclient.dto

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient

data class ClientInfoDTO(
    val id: Long,
    val name: String,
)

fun BusinessClient.toInfoDTO() =
    ClientInfoDTO(
        id = this.id,
        name = this.name,
    )
