package com.github.kkkubakkk.hobbymatchbackend.bclient.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.UpdateClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.bclient.repository.BusinessClientRepository
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.CreateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
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
            val client = businessClientOptional.get()
            return client
        }

        val businessClient =
            BusinessClient(
                name = name,
                email = email,
            )

        return businessClientRepository.save(businessClient)
    }

    fun getVenueById(id: Long): VenueDTO {
        val venueOptional = venuesRepository.findById(id)
        require(venueOptional.isPresent) { "Venue not found" }
        return venueOptional.get().toDTO()
    }

    fun addVenue(createVenueDTO: CreateVenueDTO): VenueDTO {
        val clientOptional = businessClientRepository.findById(createVenueDTO.ownerId)
        require(clientOptional.isPresent) { "Owner not found" }
        val client = clientOptional.get()
        val addedVenue =
            Venue(
                name = createVenueDTO.name,
                location = createVenueDTO.location,
                owner = client,
                hostedEvents = mutableSetOf(),
            )
        client.venues.add(addedVenue)
        venuesRepository.save(addedVenue)
        return addedVenue.toDTO()
    }

    fun getMe(id: Long): BusinessClient {
        val bClient = businessClientRepository.findById(id)
        require(bClient.isPresent) { "Not found business client: $id" }
        return bClient.get()
    }

    fun getClientById(id: Long): BusinessClientDTO {
        val clientOptional = businessClientRepository.findById(id)
        require(clientOptional.isPresent) { "Business client not found" }
        return clientOptional.get().toDTO()
    }

    fun updateClientById(
        id: Long,
        updateClientDTO: UpdateClientDTO,
    ): BusinessClientDTO {
        val clientOptional = businessClientRepository.findById(id)
        require(clientOptional.isPresent) { "Business client not found" }
        val client = clientOptional.get()
//        val newVenues = venueRepository.findAllByIdIn(updateClientDTO.venues.map { it.id })
//        require(newVenues.size == updateClientDTO.venues.size){"Some specified venues do not exist"}
//        updateVenues(client, newVenues)
        client.name = updateClientDTO.name
        client.email = updateClientDTO.email
        businessClientRepository.save(client)
        return client.toDTO()
    }

//    private fun updateVenues(
//        client: BusinessClient,
//        newVenues: List<Venue>,
//    ) {
//        val venuesToRemove = client.venues.filter { it !in newVenues }
//        venuesToRemove.forEach { venue ->
//           client.venues.remove(venue)
//        }
//
//        val venuesToAdd = newVenues.filter { it !in client.venues }
//        venuesToAdd.forEach { venue ->
//            client.venues.add(venue)
//        }
//    }
    // fun updateVenue(id: Long, updateVenueDTO: UpdateVenueDTO)

//    fun addVenue(createVenueDTO: CreateVenueDTO): VenueDTO {
//        val venue = Venue(
//            createVenueDTO.
//        )
//    }
}
