package com.github.kkkubakkk.hobbymatchbackend.bclient.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.UpdateClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.bclient.repository.BusinessClientRepository
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.CreateVenueDTO
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
import com.github.kkkubakkk.hobbymatchbackend.venue.repository.VenueRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.Optional

class BusinessClientServiceTest {
    private lateinit var clientRepository: BusinessClientRepository
    private lateinit var venueRepository: VenueRepository
    private lateinit var clientService: BusinessClientService

    val id = 10L
    val name = "John Doe"
    val email = "johndoe@example.com"
    val client =
        BusinessClient(
            id = id,
            name = name,
            email = email,
        )
    val venue =
        Venue(
            id = 0,
            location =
                Location(
                    latitude = 22.3,
                    longitude = 12.0,
                ),
            owner = client,
        )
    val updatedClient =
        BusinessClient(
            id = id,
            name = "Jean Doe",
            email = "jeandoe@example.com",
            // venues = mutableSetOf(venue),
        )
    val updateClientDTO =
        UpdateClientDTO(
            name = updatedClient.name,
            email = updatedClient.email,
            // venues = updatedClient.venues.map { it.toDTO() }
        )

    @BeforeEach
    fun setup() {
        clientRepository = mock(BusinessClientRepository::class.java)
        venueRepository = mock(VenueRepository::class.java)
        clientService = BusinessClientService(clientRepository, venueRepository)
    }

    @Test
    fun `get an existing client`() {
        // Given
        given(clientRepository.findById(id)).willReturn(Optional.of(client))
        // When
        val res = clientService.getClientById(id)
        // Then
        Assertions.assertEquals(client.toDTO(), res)
        verify(clientRepository).findById(id)
    }

    @Test
    fun `get a non-existent client`() {
        // Given
        given(clientRepository.findById(id)).willReturn(Optional.empty())
        // When
        val ex = Assertions.assertThrows(IllegalArgumentException::class.java) { clientService.getClientById(id) }
        // Then
        Assertions.assertEquals("Business client not found", ex.message)
        verify(clientRepository).findById(id)
    }

    @Test
    fun `update an existing client`() {
        // Given
        given(clientRepository.findById(id)).willReturn(Optional.of(client))
        given(venueRepository.findAllByIdIn(listOf(3))).willReturn(listOf(venue))
        // When
        val res = clientService.updateClientById(id, updateClientDTO)
        // Then
        Assertions.assertEquals(updatedClient.toDTO(), res)
        verify(clientRepository).findById(id)
        // verify(venueRepository).findAllByIdIn(listOf(3))
        verify(clientRepository).save(updatedClient)
    }

    @Test
    fun `update a non-existent client`() {
        // Given
        given(clientRepository.findById(id)).willReturn(Optional.empty())
        // When
        val ex =
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                clientService.updateClientById(id, updateClientDTO)
            }
        // Then
        Assertions.assertEquals("Business client not found", ex.message)
        verify(clientRepository).findById(id)
    }

    @Test
    fun `add venue for existing client`() {
        // Given
        val createVenueDTO =
            CreateVenueDTO(
                venue.location,
                ownerId = venue.owner.id,
                hostedActivities = emptyList(),
            )
        given(clientRepository.findById(id)).willReturn(Optional.of(client))
        // When
        val res = clientService.addVenue(createVenueDTO)
        // Then
        Assertions.assertEquals(venue.toDTO(), res)
        verify(clientRepository).findById(id)
    }

    @Test
    fun `add venue for non-existent client`() {
        // Given
        val createVenueDTO =
            CreateVenueDTO(
                venue.location,
                ownerId = venue.owner.id,
                hostedActivities = emptyList(),
            )
        given(clientRepository.findById(id)).willReturn(Optional.empty())
        // When
        val ex =
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                clientService.addVenue(createVenueDTO)
            }
        // Then
        Assertions.assertEquals("Owner not found", ex.message)
        verify(clientRepository).findById(id)
    }

    @Test
    fun `get an existing venue`() {
        // Given
        given(venueRepository.findById(id)).willReturn(Optional.of(venue))
        // When
        val res = clientService.getVenueById(id)
        // Then
        Assertions.assertEquals(venue.toDTO(), res)
        verify(venueRepository).findById(id)
    }

    @Test
    fun `get a non-existent venue`() {
        // Given
        val venueId = 4L
        given(venueRepository.findById(venueId)).willReturn(Optional.empty())
        // When
        val ex = Assertions.assertThrows(IllegalArgumentException::class.java) { clientService.getVenueById(venueId) }
        // Then
        Assertions.assertEquals("Venue not found", ex.message)
        verify(venueRepository).findById(venueId)
    }
}
