package com.github.kkkubakkk.hobbymatchbackend.bclient.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.repository.BusinessClientRepository
import org.springframework.stereotype.Service

@Service
class BusinessClientService(
    private val clientRepository: BusinessClientRepository
) {
    fun getClientById(id: Long) = clientRepository.findById(id).toDTO()
}