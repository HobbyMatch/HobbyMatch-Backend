package com.github.kkkubakkk.hobbymatchbackend.bclient.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.UpdateClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.bclient.repository.BusinessClientRepository
import com.github.kkkubakkk.hobbymatchbackend.exception.CustomRuntimeException
import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.Mockito.verify
import java.util.Optional

class BusinessClientServiceUnitTest {
    private lateinit var clientRepository: BusinessClientRepository
    private lateinit var clientService: BusinessClientService

    val client =
        BusinessClient(
            name = "John Doe",
            email = "johndoe@example.com",
            taxId = "12321",
        )

    @BeforeEach
    fun setup() {
        clientRepository = mock(BusinessClientRepository::class.java)
        clientService = BusinessClientService(clientRepository)
    }

    @Test
    fun `creating a client with existing email`() {
        // Given
        given(clientRepository.findByEmail(client.email)).willReturn(Optional.of(client))
        // When
        val res = clientService.createBusinessClient(client.email, "Jonnie Doe", taxId = "1231")
        // Then
        Assertions.assertEquals(client, res)
        verify(clientRepository).findByEmail(client.email)
    }

    @Test
    fun `creating a client with new email`() {
        // Given
        given(clientRepository.findByEmail(client.email)).willReturn(Optional.empty())
        given(clientRepository.save(client)).willReturn(client)
        // When
        val res = clientService.createBusinessClient(client.email, client.name, client.taxId)
        // Then
        Assertions.assertEquals(client, res)
        verify(clientRepository).findByEmail(client.email)
        verify(clientRepository).save(client)
    }

    @Test
    fun `get an existing client`() {
        // Given
        given(clientRepository.findById(client.id)).willReturn(Optional.of(client))
        // When
        val res = clientService.getClient(client.id)
        // Then
        Assertions.assertEquals(client, res)
        verify(clientRepository).findById(client.id)
    }

    @Test
    fun `get a non-existing client`() {
        // Given
        given(clientRepository.findById(client.id)).willReturn(Optional.empty())
        val message = "Business client with ${client.id} not found"
        // When
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                clientService.getClient(client.id)
            }
        // Then
        Assertions.assertEquals(message, ex.message)
        verify(clientRepository).findById(client.id)
    }

    @Test
    fun `update a client with no conflict`() {
        // Given
        val updateClientDTO =
            UpdateClientDTO(
                name = " Jake Doe",
                email = "jakedoe@example.com",
                taxId = "1234890",
            )
        val updatedClient = client
        updatedClient.name = updateClientDTO.name
        updatedClient.email = updateClientDTO.email
        updatedClient.taxId = updateClientDTO.taxId

        given(clientRepository.findByEmail(updateClientDTO.email)).willReturn(Optional.empty())
        given(clientRepository.findById(client.id)).willReturn(Optional.of(client))
        given(clientRepository.save(updatedClient)).willReturn(updatedClient)
        // When
        val res = clientService.updateClientById(client.id, updateClientDTO)
        // Then
        Assertions.assertEquals(updatedClient.toDTO(), res)
        verify(clientRepository).findById(client.id)
        verify(clientRepository).findByEmail(updateClientDTO.email)
        verify(clientRepository).save(updatedClient)
    }

    @Test
    fun `update a client to an existing email`() {
        // Given
        val updateClientDTO =
            UpdateClientDTO(
                name = " Jack Doe",
                email = client.email,
                taxId = "123",
            )
        val client2 =
            BusinessClient(
                name = "Jack Doe",
                email = "jackdoe@example.com",
                taxId = "123",
            )
        val message =
            "Cannot update client with id ${client2.id}, client with email '${client.email}' is already present"

        given(clientRepository.findById(client2.id)).willReturn(Optional.of(client2))
        given(clientRepository.findByEmail(updateClientDTO.email)).willReturn(Optional.of(client))
        // When
        val ex =
            Assertions.assertThrows(CustomRuntimeException::class.java) {
                clientService.updateClientById(client2.id, updateClientDTO)
            }
        // Then
        Assertions.assertEquals(message, ex.message)
        verify(clientRepository).findById(client2.id)
        verify(clientRepository).findByEmail(updateClientDTO.email)
    }
}
