package com.github.kkkubakkk.hobbymatchbackend.bclient.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.bclient.repository.BusinessClientRepository
import com.github.kkkubakkk.hobbymatchbackend.venue.repository.VenueRepository
import org.springframework.stereotype.Service

@Service
class BusinessClientService(
    private val businessClientRepository: BusinessClientRepository,
    private val venuesRepository: VenueRepository,
) {
    fun createBusinessClient(
        email: String,
        name: String,
    ): BusinessClient {
        val businessClientOptional = businessClientRepository.findByEmail(email)
        if (businessClientOptional.isPresent) {
            return businessClientOptional.get()
        }

        val businessClient =
            BusinessClient(
                name = name,
                email = email,
            )

        return businessClientRepository.save(businessClient)
    }

    fun getMe(id: Long): BusinessClient {
        val bClient = businessClientRepository.findById(id)
        require(bClient.isPresent) { "Not found business client: $id" }
        return bClient.get()
    }
}
