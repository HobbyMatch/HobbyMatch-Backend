package com.github.kkkubakkk.hobbymatchbackend.hobby.service

import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.verify

class HobbyServiceTest {
    private val hobbyRepository: HobbyRepository = mock()
    private val hobbyService: HobbyService = HobbyService(hobbyRepository)

    private val sampleHobbies =
        listOf(
            Hobby(name = "Football"),
            Hobby(name = "Chess"),
            Hobby(name = "Basketball"),
            Hobby(name = "Running"),
            Hobby(name = "Gym"),
            Hobby(name = "Swimming"),
            Hobby(name = "Fishing"),
            Hobby(name = "Tennis"),
            Hobby(name = "Hiking"),
        )

    @Test
    fun `getAllHobbies gets all hobbies`() {
        // Given
        given(hobbyRepository.findAll()).willReturn(sampleHobbies)

        // When
        val result = hobbyService.getAllHobbies()

        // Then
        verify(hobbyRepository).findAll()
        Assertions.assertEquals(sampleHobbies.size, result.size)
    }
}
