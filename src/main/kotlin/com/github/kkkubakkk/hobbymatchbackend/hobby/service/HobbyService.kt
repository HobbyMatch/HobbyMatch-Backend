package com.github.kkkubakkk.hobbymatchbackend.hobby.service

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import org.springframework.stereotype.Service

@Service
class HobbyService(
    private val hobbyRepository: HobbyRepository,
) {
    fun getAllHobbies(): List<HobbyDTO> = hobbyRepository.findAll().map { it.toDTO() }
}
