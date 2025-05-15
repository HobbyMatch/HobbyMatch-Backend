package com.github.kkkubakkk.hobbymatchbackend.exception

import org.springframework.http.HttpStatus

class NoAccessException(message: String) : CustomRuntimeException(message) {
    init {
        httpStatus = HttpStatus.FORBIDDEN
        errorCode = "ILLEGAL_ACCESS"
    }
}
