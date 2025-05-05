package com.github.kkkubakkk.hobbymatchbackend.hobby.controller

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.service.HobbyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/hobbies")
class HobbyController(
    private val hobbyService: HobbyService,
) {
    @GetMapping
    fun getAllHobbies(): List<HobbyDTO> = hobbyService.getAllHobbies()
}
