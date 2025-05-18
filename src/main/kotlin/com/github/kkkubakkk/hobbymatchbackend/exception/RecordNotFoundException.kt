package com.github.kkkubakkk.hobbymatchbackend.exception

import org.springframework.http.HttpStatus

class RecordNotFoundException(message: String) : CustomRuntimeException(message) {
    init {
        httpStatus = HttpStatus.NOT_FOUND
        errorCode = "RECORD_NOT_FOUND"
    }
}
