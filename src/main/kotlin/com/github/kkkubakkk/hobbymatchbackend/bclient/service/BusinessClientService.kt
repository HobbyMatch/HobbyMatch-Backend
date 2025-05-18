package com.github.kkkubakkk.hobbymatchbackend.bclient.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.UpdateClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.bclient.repository.BusinessClientRepository
import com.github.kkkubakkk.hobbymatchbackend.exception.CustomRuntimeException
import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import org.springframework.stereotype.Service

@Service
class BusinessClientService(
    private val businessClientRepository: BusinessClientRepository,
) {
    fun createBusinessClient(
        email: String,
        name: String,
        taxId: String,
    ): BusinessClient {
        val businessClientOptional = businessClientRepository.findByEmail(email)
        if (businessClientOptional.isPresent) {
            return businessClientOptional.get()
        }

        val businessClient =
            BusinessClient(
                name = name,
                email = email,
                taxId = taxId,
            )

        return businessClientRepository.save(businessClient)
    }

    fun getClient(id: Long): BusinessClient {
        val clientOptional = businessClientRepository.findById(id)
        if (clientOptional.isEmpty) {
            throw RecordNotFoundException("Business client with $id not found")
        }
        return clientOptional.get()
    }

    fun saveClient(client: BusinessClient) {
        businessClientRepository.save(client)
    }

    // td: verify if needs fixing!!!
    fun updateClientById(
        id: Long,
        updateClientDTO: UpdateClientDTO,
    ): BusinessClientDTO {
        val client = getClient(id)
        val email = updateClientDTO.email
        val existingClient = businessClientRepository.findByEmail(email)
        if (existingClient.isPresent) {
            throw CustomRuntimeException(
                "Cannot update client with id $id, client with email '$email' is already present",
            )
        }
        client.name = updateClientDTO.name
        client.email = email
        client.taxId = updateClientDTO.taxId
        return businessClientRepository.save(client).toDTO()
    }
}
