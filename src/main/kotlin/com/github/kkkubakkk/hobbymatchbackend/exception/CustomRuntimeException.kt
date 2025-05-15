package com.github.kkkubakkk.hobbymatchbackend.exception

import org.springframework.http.HttpStatus

open class CustomRuntimeException(message: String) : RuntimeException(message) {
    var httpStatus: HttpStatus = HttpStatus.NOT_FOUND
        internal set
    var errorCode: String = "NOT_FOUND"
        internal set
}
