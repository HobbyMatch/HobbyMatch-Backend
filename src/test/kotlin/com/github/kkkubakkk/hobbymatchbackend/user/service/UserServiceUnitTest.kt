package com.github.kkkubakkk.hobbymatchbackend.user.service

import com.github.kkkubakkk.hobbymatchbackend.exception.CustomRuntimeException
import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.HobbyDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.Optional

class UserServiceUnitTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService
    private lateinit var hobbyRepository: HobbyRepository

    private val user =
        User(
            id = 1,
            name = "Jonnie Doe",
            email = "john.doe@example.com",
        )

    @BeforeEach
    fun setup() {
        userRepository = mock(UserRepository::class.java)
        hobbyRepository = mock(HobbyRepository::class.java)
        userService = UserService(userRepository, hobbyRepository)
    }

    @Test
    fun `creating a user with existing email`() {
        // Given
        given(userRepository.findByEmail(user.email)).willReturn(Optional.of(user))
        // When
        val res = userService.createUser(user.email, "Jonnie Doe")
        // Then
        Assertions.assertEquals(user, res)
        verify(userRepository).findByEmail(user.email)
    }

    @Test
    fun `creating a user with new email`() {
        // Given
        given(userRepository.findByEmail(user.email)).willReturn(Optional.empty())
        given(userRepository.save(any(User::class.java))).willReturn(user)
        // When
        val res = userService.createUser(user.email, user.name)
        // Then
        Assertions.assertEquals(user, res)
        verify(userRepository).findByEmail(user.email)
        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `get an existing user`() {
        // Given
        given(userRepository.findById(user.id)).willReturn(Optional.of(user))
        // When
        val res = userService.getUser(user.id)
        // Then
        Assertions.assertEquals(user, res)
        verify(userRepository).findById(user.id)
    }

    @Test
    fun `get a non-existing user`() {
        // Given
        given(userRepository.findById(user.id)).willReturn(Optional.empty())
        val message = "User not found"
        // When
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                userService.getUser(user.id)
            }
        // Then
        Assertions.assertEquals(message, ex.message)
        verify(userRepository).findById(user.id)
    }

    @Test
    fun `update a user with no conflicts`() {
        // Given
        val hobby1 = Hobby(id = 1, name = "Swimming")
        val hobby2 = Hobby(id = 2, name = "Reading")

        val updateUserDTO =
            UpdateUserDTO(
                name = "Jake Doe",
                email = "jakedoe@example.com",
                hobbies =
                    listOf(
                        HobbyDTO(name = "Swimming"),
                        HobbyDTO(name = "Reading"),
                    ),
            )

        val updatedUser = user.copy()
        updatedUser.name = updateUserDTO.name
        updatedUser.email = updateUserDTO.email
        updatedUser.hobbies = mutableSetOf(hobby1, hobby2)

        given(userRepository.findById(user.id)).willReturn(Optional.of(user))
        given(hobbyRepository.findByName("Swimming")).willReturn(Optional.of(hobby1))
        given(hobbyRepository.findByName("Reading")).willReturn(Optional.of(hobby2))
        given(userRepository.save(user)).willReturn(updatedUser)

        // When
        val result = userService.updateUser(user.id, updateUserDTO)

        // Then
        Assertions.assertEquals(updatedUser, result)
        Assertions.assertEquals(2, result.hobbies.size)
        verify(userRepository).findById(user.id)
        verify(hobbyRepository).findByName("Swimming")
        verify(hobbyRepository).findByName("Reading")
        verify(userRepository).save(user)
    }

    @Test
    fun `update a user to an existing email`() {
        // Given
        val updateUserDTO =
            UpdateUserDTO(
                name = "Jack Doe",
                email = "john.doe@example.com",
                hobbies = emptyList(),
            )

        val user2 =
            User(
                id = 2,
                name = "Jack Doe",
                email = "jack.doe@example.com",
            )

        val message =
            "Cannot update user with id ${user2.id}, user with email '${user.email}' is already present"

        given(userRepository.findById(user2.id)).willReturn(Optional.of(user2))
        given(userRepository.findByEmail(updateUserDTO.email)).willReturn(Optional.of(user))

        // When
        val ex =
            Assertions.assertThrows(CustomRuntimeException::class.java) {
                userService.updateUser(user2.id, updateUserDTO)
            }

        // Then
        Assertions.assertEquals(message, ex.message)
        verify(userRepository).findById(user2.id)
        verify(userRepository).findByEmail(updateUserDTO.email)
    }

    @Test
    fun `update a user with a non-existing hobby`() {
        // Given
        val updateUserDTO =
            UpdateUserDTO(
                name = "Jake Doe",
                email = "jakedoe@example.com",
                hobbies =
                    listOf(
                        HobbyDTO(name = "NonExistingHobby"),
                    ),
            )

        given(userRepository.findById(user.id)).willReturn(Optional.of(user))
        given(hobbyRepository.findByName("NonExistingHobby")).willReturn(Optional.empty())

        // When & Then
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                userService.updateUser(user.id, updateUserDTO)
            }

        Assertions.assertEquals("Hobby not found", ex.message)
        verify(userRepository).findById(user.id)
        verify(hobbyRepository).findByName("NonExistingHobby")
    }
}
