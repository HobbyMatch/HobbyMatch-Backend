package com.github.kkkubakkk.hobbymatchbackend.exception

import java.time.LocalDateTime

data class ErrorDetails(
    val timestamp: LocalDateTime,
    val message: String,
    val details: String,
    val errorCode: String,
)
