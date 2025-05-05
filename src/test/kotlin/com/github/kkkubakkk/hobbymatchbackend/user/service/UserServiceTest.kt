package com.github.kkkubakkk.hobbymatchbackend.user.service

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.user.dto.CreateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.LocalDate
import java.util.Optional

class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var hobbyRepository: HobbyRepository
    private lateinit var userService: UserService

    private val id = 1L
    private val firstName = "John"
    private val lastName = "Doe"
    private val username = "johndoe"
    private val email = "johndoe@example.com"
    private var hobby =
        Hobby(
            id = 1L,
            name = "Volleyball",
            users = mutableSetOf(),
            activities = mutableSetOf(),
        )
    private val birthday = LocalDate.of(1989, 8, 19)
    private val bio = "just another user bio..."

    private val user =
        User(
            id,
            firstName,
            lastName,
            username,
            email,
            mutableSetOf(hobby),
            birthday,
            bio,
        )

    @BeforeEach
    fun setup() {
        userRepository = mock(UserRepository::class.java)
        hobbyRepository = mock(HobbyRepository::class.java)
        userService = UserService(userRepository, hobbyRepository)
        //
        hobby.users.add(user)
    }

    @Test
    fun `create user test`() {
        // Given
        val createdUser =
            User(
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
                hobbies = mutableSetOf(hobby),
                birthday = birthday,
                bio = bio,
            )
        val createUserDTO =
            CreateUserDTO(
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
                hobbies = listOf(hobby.toDTO()),
                birthday = birthday.toString(),
                bio = bio,
            )
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        given(userRepository.save(createdUser)).willReturn(createdUser)
        given(hobbyRepository.findAllByNameIn(createUserDTO.hobbies.map { it.name })).willReturn(listOf(hobby))
        // When
        val res = userService.createUser(createUserDTO)
        // Then
        Assertions.assertEquals(createdUser.toDTO(), res)
        verify(userRepository).findByEmail(email)
        verify(userRepository).save(createdUser)
    }
    // TODO: verify createUser throws an exception "User with this email already exists"
    // TODO: verify createUser throws an exception "Some specified hobbies do not exist"

    @Test
    fun `verifying if user exists for an existent user`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        // When
        val res = userService.userExists(email)
        // Then
        Assertions.assertTrue(res)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `verifying if user exists for a non-existent user`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        // When
        val res = userService.userExists(email)
        // Then
        Assertions.assertFalse(res)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `find an OAuth user`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        Assertions.assertEquals(userService.findOrCreateOAuthUser(email, firstName, lastName), user)
        // When
        val foundUser = userService.findOrCreateOAuthUser(email, firstName, lastName)
        // Then
        Assertions.assertEquals(user, foundUser)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `create an OAuth user with a username + counter`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        val counter = 1
        val diffUser =
            User(
                id,
                "Laura",
                lastName,
                username,
                email,
                mutableSetOf(hobby),
                null,
                null,
            )
        given(userRepository.findByUsername(username)).willReturn(Optional.of(diffUser))
        val newUsername = "$username$counter"
        val newUser =
            User(
                firstName = firstName,
                lastName = lastName,
                username = newUsername,
                email = email,
            )
        given(userRepository.save(newUser)).willReturn(newUser)
        // When
        val createdUser = userService.findOrCreateOAuthUser(email, firstName, lastName)
        // Then
        Assertions.assertEquals(newUser, createdUser)
        verify(userRepository).findByEmail(email)
        verify(userRepository).findByUsername(username)
        verify(userRepository).findByUsername(newUsername)
        verify(userRepository).save(newUser)
    }

    @Test
    fun `get an existing user by email`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        // When
        val res = userService.getUserByEmail(email)
        // Then
        Assertions.assertEquals(user.toDTO(), res)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `exception thrown when getting a non-existent user by email`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        // Then
        val ex =
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                userService.getUserByEmail(email)
            }
        Assertions.assertEquals("User not found", ex.message)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `get an existing user by username`() {
        // Given
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user))
        // When
        val res = userService.getUserByUsername(username)
        // Then
        Assertions.assertEquals(user.toDTO(), res)
        verify(userRepository).findByUsername(username)
    }

    @Test
    fun `exception thrown when getting a non-existent user by username`() {
        // Given
        given(userRepository.findByUsername(username)).willReturn(Optional.empty())
        // Then
        val ex =
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                userService.getUserByUsername(username)
            }
        Assertions.assertEquals("User not found", ex.message)
        verify(userRepository).findByUsername(username)
    }

    // TODO: updateUserByEmail test for an existing user
    // TODO: updateUSerByEmail throws exception "User not found"
    // TODO: updateUSerByEmail throws exception "Some specified hobbies do not exist"

    @Test
    fun `get all users`() {
        // Given
        val allUsers = listOf(user)
        given(userRepository.findAll()).willReturn(allUsers)
        // When
        val res = userService.getAllUsers()
        // Then
        Assertions.assertEquals(allUsers.map { it.toDTO() }, res)
        verify(userRepository).findAll()
    }

    // TODO: updateUserByUsername test for an existing user
    // TODO: updateUserByUsername throws exception "User not found"

    // TODO: updateHobbies test - needs some exceptions  added to be thrown??

    @Test
    fun `activate an existent user`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        val activatedUser =
            User(
                id,
                firstName,
                lastName,
                username,
                email,
                mutableSetOf(hobby),
                birthday,
                bio,
                isActive = true,
            )
        given(userRepository.save(activatedUser)).willReturn(activatedUser)
        Assertions.assertEquals(activatedUser.toDTO(), userService.activateUser(email))
        // When
        val res = userService.activateUser(email)
        // Then
        Assertions.assertEquals(activatedUser.toDTO(), res)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `exception thrown when activating a non-existent user`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        // Then
        val ex =
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                userService.activateUser(email)
            }
        Assertions.assertEquals("User not found", ex.message)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `deactivate an existent user`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        val deactivatedUser =
            User(
                id,
                firstName,
                lastName,
                username,
                email,
                mutableSetOf(hobby),
                birthday,
                bio,
                isActive = false,
            )
        given(userRepository.save(deactivatedUser)).willReturn(deactivatedUser)
        // When
        val res = userService.deactivateUser(email)
        // Then
        Assertions.assertEquals(deactivatedUser.toDTO(), res)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `exception thrown when deactivating a non-existent user`() {
        // Given
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        // Then
        val ex =
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                userService.deactivateUser(email)
            }
        Assertions.assertEquals("User not found", ex.message)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `get all users for an empty database`() {
        // Given
        val allUsers = emptyList<User>()
        given(userRepository.findAll()).willReturn(allUsers)
        // When
        val res = userService.getAllUsers()
        // Then
        Assertions.assertEquals(allUsers.map { it.toDTO() }, res)
        verify(userRepository).findAll()
    }

    // TODO: searchAllUsers tests
}
