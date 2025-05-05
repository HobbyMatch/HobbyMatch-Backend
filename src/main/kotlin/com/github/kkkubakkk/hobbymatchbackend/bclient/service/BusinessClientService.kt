package com.github.kkkubakkk.hobbymatchbackend.bclient.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.UpdateClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.repository.BusinessClientRepository
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.CreateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
import com.github.kkkubakkk.hobbymatchbackend.venue.repository.VenueRepository
import org.springframework.stereotype.Service

@Service
class BusinessClientService(
    private val clientRepository: BusinessClientRepository,
    private val venueRepository: VenueRepository,
) {
    fun getClientById(id: Long): BusinessClientDTO {
        val clientOptional = clientRepository.findById(id)
        require(clientOptional.isPresent) { "Business client not found" }
        return clientOptional.get().toDTO()
    }

    fun updateClientById(
        id: Long,
        updateClientDTO: UpdateClientDTO,
    ): BusinessClientDTO {
        val clientOptional = clientRepository.findById(id)
        require(clientOptional.isPresent) { "Business client not found" }
        val client = clientOptional.get()
//        val newVenues = venueRepository.findAllByIdIn(updateClientDTO.venues.map { it.id })
//        require(newVenues.size == updateClientDTO.venues.size){"Some specified venues do not exist"}
//        updateVenues(client, newVenues)
        client.name = updateClientDTO.name
        client.email = updateClientDTO.email
        clientRepository.save(client)
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

    fun addVenue(createVenueDTO: CreateVenueDTO): VenueDTO {
        val clientOptional = clientRepository.findById(createVenueDTO.ownerId)
        require(clientOptional.isPresent) { "Owner not found" }
        val client = clientOptional.get()
        val addedVenue =
            Venue(
                location = createVenueDTO.location,
                owner = client,
                hostedActivities = mutableSetOf(),
            )
        client.venues.add(addedVenue)
        venueRepository.save(addedVenue)
        return addedVenue.toDTO()
    }

    fun getVenueById(id: Long): VenueDTO {
        val venueOptional = venueRepository.findById(id)
        require(venueOptional.isPresent) { "Venue not found" }
        return venueOptional.get().toDTO()
    }

    fun removeVenueById(id: Long) {
        val venueOptional = venueRepository.findById(id)
        if (venueOptional.isEmpty) {
            return
        }
        val venue = venueOptional.get()
        val clientOptional = clientRepository.findById(venue.owner.id)
        require(clientOptional.isPresent) { "Owner not found" }
        val client = clientOptional.get()
        client.venues.remove(venue)
        venueRepository.delete(venue)
    }

    // fun updateVenue(id: Long, updateVenueDTO: UpdateVenueDTO)

//    fun addVenue(createVenueDTO: CreateVenueDTO): VenueDTO {
//        val venue = Venue(
//            createVenueDTO.
//        )
//    }
}
