package com.github.kkkubakkk.hobbymatchbackend.infrastructure.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/hello")
    fun hello(): String = HELLO

    companion object {
        private const val HELLO =

            "Hello, world!"
    }
}
