package com.github.kkkubakkk.hobbymatchbackend.venue.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import com.github.kkkubakkk.hobbymatchbackend.exception.CustomRuntimeException
import com.github.kkkubakkk.hobbymatchbackend.exception.NoAccessException
import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.CreateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.UpdateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
import com.github.kkkubakkk.hobbymatchbackend.venue.repository.VenueRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.Mockito.verify
import java.util.Optional

class VenueServiceUnitTest {
    private lateinit var venueRepository: VenueRepository
    private lateinit var venueService: VenueService
    private lateinit var clientService: BusinessClientService

    val client =
        BusinessClient(
            name = "John Doe",
            email = "johndoe@example.com",
            taxId = "12321",
        )

    val venue =
        Venue(
            name = "Aquatic world",
            description = "A 5x20m indoor swimming pool",
            address = "Example Street 13, Exampletown",
            location =
                Location(
                    longitude = 34.6,
                    latitude = 32.2,
                ),
            owner = client,
            events = mutableSetOf(),
        )

    val venue2 =
        Venue(
            name = "Chaotic world",
            description = "A trampoline park",
            address = "Example Street 2, New Exampletown",
            location =
                Location(
                    longitude = 34.6,
                    latitude = 32.2,
                ),
            owner = client,
            events = mutableSetOf(),
        )

    val addedVenue =
        CreateVenueDTO(
            name = venue.name,
            description = venue.description,
            address = venue.address,
            location = venue.location,
        )

    @BeforeEach
    fun setup() {
        clientService = mock(BusinessClientService::class.java)
        venueRepository = mock(VenueRepository::class.java)
        venueService = VenueService(venueRepository, clientService)
    }

    @Test
    fun `add a venue for an existing client`() {
        // Given
        given(clientService.getClient(client.id)).willReturn(client)
        client.venues.add(venue)
        given(venueRepository.save(venue)).willReturn(venue)
        // When
        val res = venueService.addVenue(client.id, addedVenue)
        // Then
        Assertions.assertEquals(venue.toDTO(), res)
        verify(clientService).getClient(client.id)
        verify(clientService).saveClient(client)
        verify(venueRepository).save(venue)
    }

    @Test
    fun `add a venue for a non-existing client`() {
        // Given
        val message = "Business client not found"
        given(clientService.getClient(client.id)).willThrow(RecordNotFoundException(message))
        // When
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                venueService.addVenue(client.id, addedVenue)
            }
        // Then
        Assertions.assertEquals(message, ex.message)
        verify(clientService).getClient(client.id)
    }

    @Test
    fun `get an existing venue`() {
        // Given
        given(venueRepository.findById(venue.id)).willReturn(Optional.of(venue))
        // When
        val res = venueService.getVenue(venue.id)
        // Then
        Assertions.assertEquals(venue, res)
        verify(venueRepository).findById(venue.id)
    }

    @Test
    fun `get a non-existing venue`() {
        // Given
        val message = "Venue with id ${venue.id} was not found"
        given(venueRepository.findById(venue.id)).willReturn(Optional.empty())
        // When
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                venueService.getVenue(venue.id)
            }
        // Then
        Assertions.assertEquals(message, ex.message)
        verify(venueRepository).findById(venue.id)
    }

    @Test
    fun `get all venues`() {
        // Given
        val list = listOf(venue, venue2)
        given(venueRepository.findAll()).willReturn(list)
        // When
        val res = venueService.getAllVenues()
        // Then
        Assertions.assertEquals(list.map { it.toDTO() }, res)
        verify(venueRepository).findAll()
    }

    @Test
    fun `verify access for a user`() {
        // Given
        val okUserId = client.id
        val notOkUserId = client.id + 1

        val message = "Access denied for client with id $notOkUserId, expected client with id ${client.id}"
        // When
        val ex =
            Assertions.assertThrows(NoAccessException::class.java) {
                venueService.verifyAccess(client.id, notOkUserId)
            }
        // Then
        Assertions.assertDoesNotThrow {
            venueService.verifyAccess(client.id, okUserId)
        }
        Assertions.assertEquals(message, ex.message)
    }

    @Test
    fun `delete an existing owned venue`() {
        // Given
        given(venueRepository.findById(venue.id)).willReturn(Optional.of(venue))
        // Then
        Assertions.assertDoesNotThrow { venueService.deleteVenue(venue.id, client.id) }
        verify(venueRepository).findById(venue.id)
    }

    @Test
    fun `delete a non-existing venue`() {
        // Given
        given(venueRepository.findById(venue.id)).willReturn(Optional.empty())
        // When
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                venueService.deleteVenue(venue.id, client.id)
            }
        // Then
        Assertions.assertEquals("Venue with id ${venue.id} was not found", ex.message)
        verify(venueRepository).findById(venue.id)
    }

    @Test
    fun `delete an existing not owned venue`() {
        // Given
        given(venueRepository.findById(venue.id)).willReturn(Optional.of(venue))
        val loggedInClientId = client.id + 1
        val message = "Access denied for client with id $loggedInClientId, expected client with id ${client.id}"
        // When
        val ex =
            Assertions.assertThrows(NoAccessException::class.java) {
                venueService.deleteVenue(venue.id, loggedInClientId)
            }
        // Then
        Assertions.assertEquals(message, ex.message)
        verify(venueRepository).findById(venue.id)
    }

    @Test
    fun `update a venue with no conflict`() {
        // Given
        val updateVenueDTO =
            UpdateVenueDTO(
                name = "New Aquatic world",
                description = "Same old 5x20m indoor pool",
            )
        val updatedVenue =
            Venue(
                name = updateVenueDTO.name,
                description = updateVenueDTO.description,
                address = "Example Street 13, Exampletown",
                location =
                    Location(
                        longitude = 34.6,
                        latitude = 32.2,
                    ),
                owner = client,
                events = mutableSetOf(),
            )
        given(venueRepository.findById(venue.id)).willReturn(Optional.of(venue))
        given(venueRepository.findByName(updateVenueDTO.name)).willReturn(Optional.empty())
        given(venueRepository.save(updatedVenue)).willReturn(updatedVenue)
        // When
        val res = venueService.updateVenue(venue.id, client.id, updateVenueDTO)
        // Then
        Assertions.assertEquals(updatedVenue.toDTO(), res)
        verify(venueRepository).findById(venue.id)
        verify(venueRepository).findByName(venue.name)
        verify(venueRepository).save(updatedVenue)
    }

    @Test
    fun `update a venue to an existing name`() {
        // Given
        val updateVenueDTO =
            UpdateVenueDTO(
                name = venue.name,
                description = "Same old trampoline park",
            )
        val message = "Cannot update venue, venue with name ${updateVenueDTO.name} already present"
        given(venueRepository.findById(venue2.id)).willReturn(Optional.of(venue2))
        given(venueRepository.findByName(updateVenueDTO.name)).willReturn(Optional.of(venue))
        // When
        val ex =
            Assertions.assertThrows(CustomRuntimeException::class.java) {
                venueService.updateVenue(venue2.id, client.id, updateVenueDTO)
            }
        // Then
        Assertions.assertEquals(message, ex.message)
        verify(venueRepository).findById(venue2.id)
        verify(venueRepository).findByName(updateVenueDTO.name)
    }
}
