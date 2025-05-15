package com.github.kkkubakkk.hobbymatchbackend.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {
    // Custom exception handling
    @ExceptionHandler(CustomRuntimeException::class)
    fun handleCustomRuntimeException(
        exc: CustomRuntimeException,
        webRequest: WebRequest,
    ): ResponseEntity<ErrorDetails> {
        val errorDetails =
            ErrorDetails(
                timestamp = LocalDateTime.now(),
                message = exc.message.toString(),
                details = webRequest.getDescription(false),
                errorCode = exc.errorCode,
            )
        return ResponseEntity(errorDetails, exc.httpStatus)
    }

    // Generic exception handling
    @ExceptionHandler(Exception::class)
    fun handleException(
        exc: Exception,
        webRequest: WebRequest,
    ): ResponseEntity<ErrorDetails> {
        val errorDetails =
            ErrorDetails(
                timestamp = LocalDateTime.now(),
                message = exc.message.toString(),
                details = webRequest.getDescription(false),
                errorCode = "INTERNAL_SERVER_ERROR",
            )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
