package com.github.kkkubakkk.hobbymatchbackend.venue.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import com.github.kkkubakkk.hobbymatchbackend.exception.CustomRuntimeException
import com.github.kkkubakkk.hobbymatchbackend.exception.NoAccessException
import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.CreateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.UpdateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
import com.github.kkkubakkk.hobbymatchbackend.venue.repository.VenueRepository
import org.springframework.stereotype.Service

@Service
class VenueService(
    private val venueRepository: VenueRepository,
    private val clientService: BusinessClientService,
) {
    fun addVenue(
        clientId: Long,
        createVenueDTO: CreateVenueDTO,
    ): VenueDTO {
        val client = clientService.getClient(clientId)
        val addedVenue =
            Venue(
                name = createVenueDTO.name,
                description = createVenueDTO.description,
                address = createVenueDTO.address,
                location = createVenueDTO.location,
                owner = client,
                events = mutableSetOf(),
            )
        client.venues.add(addedVenue)
        clientService.saveClient(client)
        return venueRepository.save(addedVenue).toDTO()
    }

    fun getVenue(id: Long): Venue {
        // returning Venue and not VenueDTO
        // to reuse the fun in the other service functions,
        // e.g. when updating/deleting the venue
        val venueOptional = venueRepository.findById(id)
        if (venueOptional.isEmpty) {
            throw RecordNotFoundException("Venue with id $id was not found")
        }
        return venueOptional.get()
    }

    fun getAllVenues(): List<VenueDTO> {
        val venues = venueRepository.findAll()
        return venues.map { it.toDTO() }
    }

    fun deleteVenue(
        venueId: Long,
        clientId: Long,
    ) {
        val venue = getVenue(venueId)
        verifyAccess(venue.owner.id, clientId)
        venueRepository.delete(venue)
    }

    fun updateVenue(
        venueId: Long,
        clientId: Long,
        updateVenueDTO: UpdateVenueDTO,
    ): VenueDTO {
        val venue = getVenue(venueId)
        verifyAccess(venue.owner.id, clientId)
        val name = updateVenueDTO.name
        if (venueRepository.findByName(name).isPresent) {
            throw CustomRuntimeException("Cannot update venue, venue with name $name already present")
        }
        venue.name = name
        venue.description = updateVenueDTO.description
        return venueRepository.save(venue).toDTO()
    }

    fun verifyAccess(
        ownerId: Long,
        clientId: Long,
    ) {
        if (clientId != ownerId) {
            throw NoAccessException(
                "Access denied for client with id $clientId, expected client with id $ownerId",
            )
        }
    }
}
