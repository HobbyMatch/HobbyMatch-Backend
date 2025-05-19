package com.github.kkkubakkk.hobbymatchbackend.user.service

import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.verify
import java.util.Optional

class UserServiceTest {
    private val userRepository: UserRepository = mock()
    private val hobbyRepository: HobbyRepository = mock()
    private val userService: UserService = UserService(userRepository, hobbyRepository)

    private val sampleUser = User(id = 1L, name = "John", email = "john@example.com")

    @Test
    fun `create user should return existing user if email already exists`() {
        // Given
        given(userRepository.findByEmail(sampleUser.email)).willReturn(Optional.of(sampleUser))

        // When
        val result = userService.createUser(sampleUser.email, sampleUser.name)

        // Then
        verify(userRepository).findByEmail(sampleUser.email)
        Assertions.assertEquals(sampleUser, result)
    }

    @Test
    fun `create user should create and return new user if email does not exist`() {
        // Given
        given(userRepository.findByEmail(sampleUser.email)).willReturn(Optional.empty())
        given(userRepository.save(any())).willAnswer { it.arguments[0] }

        // When
        val result = userService.createUser(sampleUser.email, sampleUser.name)

        // Then
        verify(userRepository).findByEmail(sampleUser.email)
        verify(userRepository).save(any())
        Assertions.assertEquals(sampleUser.name, result.name)
        Assertions.assertEquals(sampleUser.email, result.email)
    }

    @Test
    fun `get user should return user when user exists`() {
        // Given
        given(userRepository.findById(sampleUser.id)).willReturn(Optional.of(sampleUser))

        // When
        val result = userService.getUser(sampleUser.id)

        // Then
        verify(userRepository).findById(sampleUser.id)
        Assertions.assertEquals(sampleUser, result)
    }

    @Test
    fun `get user should throw exception when user does not exist`() {
        // Given
        val wrongUserId = 100L
        given(userRepository.findById(wrongUserId)).willReturn(Optional.empty())

        // Then
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                userService.getUser(wrongUserId)
            }

        verify(userRepository).findById(wrongUserId)
        Assertions.assertEquals("User not found", ex.message)
    }

    @Test
    fun `update user should update name, email and hobbies`() {
        // Given
        val newHobby = Hobby(name = "Running")
        val updateUserDto =
            UpdateUserDTO(
                name = "Jane",
                email = "jane@example.com",
                hobbies = listOf(newHobby.toDTO()),
            )

        given(userRepository.findById(sampleUser.id)).willReturn(Optional.of(sampleUser))
        given(hobbyRepository.findByName("Running")).willReturn(Optional.of(newHobby))
        given(userRepository.save(any())).willAnswer { it.arguments[0] }

        // When
        val result = userService.updateUser(sampleUser.id, updateUserDto)

        // Then
        verify(userRepository).findById(sampleUser.id)
        verify(hobbyRepository).findByName("Running")
        verify(userRepository).save(sampleUser)
        Assertions.assertEquals(updateUserDto.name, result.name)
        Assertions.assertEquals(updateUserDto.email, result.email)
        Assertions.assertTrue(result.hobbies.contains(newHobby))
        result.hobbies.forEach {
            Assertions.assertTrue(it.users.contains(sampleUser))
        }
    }

    @Test
    fun `update user should throw if hobby not found`() {
        // Given
        val missingHobbyName = "Skydiving"
        val updateDto =
            UpdateUserDTO(
                name = "Jane",
                email = "jane@example.com",
                hobbies = listOf(Hobby(name = missingHobbyName).toDTO()),
            )

        given(userRepository.findById(sampleUser.id)).willReturn(Optional.of(sampleUser))
        given(hobbyRepository.findByName(missingHobbyName)).willReturn(Optional.empty())

        // Then
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                userService.updateUser(sampleUser.id, updateDto)
            }

        verify(hobbyRepository).findByName(missingHobbyName)
        Assertions.assertEquals("Hobby not found", ex.message)
    }
}
