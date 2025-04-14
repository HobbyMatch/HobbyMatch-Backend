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
import org.mockito.BDDMockito.*
import java.time.LocalDate

import java.util.*

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var hobbyRepository: HobbyRepository
    private lateinit var userService: UserService

    private val id = 1L
    private val firstName = "John"
    private val lastName = "Doe"
    private val username = "johndoe"
    private val email = "johndoe@example.com"
    private var hobby = Hobby(
        id = 1L,
        name = "Volleyball",
        users = mutableSetOf(),
        activities = mutableSetOf()
    )
    private val birthday = LocalDate.of(1989,8,19)
    private val bio = "just another user bio..."

    private val user = User(
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
        val createdUser = User(
            firstName = firstName,
            lastName = lastName,
            username = username,
            email = email,
            hobbies = mutableSetOf(hobby),
            birthday = birthday,
            bio = bio,
        )
        val createUserDTO = CreateUserDTO(
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
        val res = userService.createUser(createUserDTO)
        Assertions.assertEquals(createdUser.toDTO(), res)
        verify(userRepository).findByEmail(email)
        verify(userRepository).save(createdUser)
    }
    // TODO: verify createUser throws an exception "User with this email already exists"
    // TODO: verify createUser throws an exception "Some specified hobbies do not exist"

    @Test
    fun `verifying if user exists for an existent user`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        Assertions.assertTrue(userService.userExists(email))
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `verifying if user exists for a non-existent user`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        Assertions.assertFalse(userService.userExists(email))
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `find an OAuth user`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        Assertions.assertEquals(userService.findOrCreateOAuthUser(email,firstName,lastName),user)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `create an OAuth user with a username + counter`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        val counter = 1;
        val diffUser = User(
            id,
            "Laura",
            lastName,
            username,
            email,
            mutableSetOf(hobby),
            null,
            null,
        )
        given (userRepository.findByUsername(username)).willReturn(Optional.of(diffUser))
        val newUsername = "$username$counter"
        val newUser =
            User(
                firstName = firstName,
                lastName = lastName,
                username = newUsername,
                email = email,
            )
        given(userRepository.save(newUser)).willReturn(newUser)
        Assertions.assertEquals(newUser, userService.findOrCreateOAuthUser(email, firstName, lastName))
        verify(userRepository).findByEmail(email)
        verify(userRepository).findByUsername(username)
        verify(userRepository).findByUsername(newUsername)
        verify(userRepository).save(newUser)
    }

    @Test
    fun `get an existing user by email`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        Assertions.assertEquals(user.toDTO(), userService.getUserByEmail(email))
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `exception thrown when getting a non-existent user by email`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        val ex = Assertions.assertThrows(IllegalArgumentException::class.java)
        {
            userService.getUserByEmail(email)
        }
        Assertions.assertEquals("User not found",ex.message)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `get an existing user by username`() {
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user))
        Assertions.assertEquals(user.toDTO(), userService.getUserByUsername(username))
        verify(userRepository).findByUsername(username)
    }

    @Test
    fun `exception thrown when getting a non-existent user by username`() {
        given(userRepository.findByUsername(username)).willReturn(Optional.empty())
        val ex = Assertions.assertThrows(IllegalArgumentException::class.java)
        {
            userService.getUserByUsername(username)
        }
        Assertions.assertEquals("User not found",ex.message)
        verify(userRepository).findByUsername(username)
    }

    // TODO: updateUserByEmail test for an existing user
    // TODO: updateUSerByEmail throws exception "User not found"
    // TODO: updateUSerByEmail throws exception "Some specified hobbies do not exist"

    @Test
    fun `get all users`() {
        val allUsers = listOf(user)
        given(userRepository.findAll()).willReturn(allUsers)
        Assertions.assertEquals(allUsers.map { it.toDTO() }, userService.getAllUsers())
        verify(userRepository).findAll()
    }

    // TODO: updateUserByUsername test for an existing user
    // TODO: updateUserByUsername throws exception "User not found"

    // TODO: updateHobbies test - needs some exceptions  added to be thrown??

    @Test
    fun `activate an existent user`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        val activatedUser = User(
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
    verify(userRepository).findByEmail(email)
    }

    @Test
    fun `exception thrown when activating a non-existent user`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        val ex = Assertions.assertThrows(IllegalArgumentException::class.java)
        {
            userService.activateUser(email)
        }
        Assertions.assertEquals("User not found",ex.message)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `deactivate an existent user`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user))
        val deactivatedUser = User(
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
        Assertions.assertEquals(deactivatedUser.toDTO(), userService.deactivateUser(email))
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `exception thrown when deactivating a non-existent user`() {
        given(userRepository.findByEmail(email)).willReturn(Optional.empty())
        val ex = Assertions.assertThrows(IllegalArgumentException::class.java)
        {
            userService.deactivateUser(email)
        }
        Assertions.assertEquals("User not found",ex.message)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `get all users for an empty database`() {
        val allUsers = emptyList<User>()
        given(userRepository.findAll()).willReturn(allUsers)
        Assertions.assertEquals(allUsers.map { it.toDTO() }, userService.getAllUsers())
        verify(userRepository).findAll()
    }

    // TODO: searchAllUsers tests
}