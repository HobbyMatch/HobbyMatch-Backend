package com.github.kkkubakkk.hobbymatchbackend.user.service

import com.github.kkkubakkk.hobbymatchbackend.user.dto.CreateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.EmailUpdateDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.SearchUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun createUser(createUserDTO: CreateUserDTO): UserDTO {
        val existingUser = userRepository.findByEmail(createUserDTO.email)
        require(!existingUser.isPresent) { "User with this email already exists" }

        val user =
            User(
                firstName = createUserDTO.firstName,
                lastName = createUserDTO.lastName,
                username = createUserDTO.username,
                email = createUserDTO.email,
            )
        userRepository.save(user)
        return user.toDTO()
    }

    fun userExists(email: String): Boolean = userRepository.findByEmail(email).isPresent

    fun getUserById(id: Long): UserDTO {
        val userOptional = userRepository.findById(id)
        require(userOptional.isPresent) { "User not found" }
        return userOptional.get().toDTO()
    }

    fun findOrCreateOAuthUser(
        email: String,
        firstName: String,
        lastName: String,
    ): User {
        val userOptional = userRepository.findByEmail(email)

        if (userOptional.isPresent) {
            return userOptional.get()
        }

        // Generate a unique username based on email
        val baseUsername = email.substringBefore("@")
        var username = baseUsername
        var counter = 1

        while (userRepository.findByUsername(username).isPresent) {
            username = "$baseUsername$counter"
            counter++
        }

        val user =
            User(
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
            )

        return userRepository.save(user)
    }

    fun getUserByEmail(email: String): UserDTO {
        val userOptional = userRepository.findByEmail(email)
        require(userOptional.isPresent) { "User not found" }
        return userOptional.get().toDTO()
    }

    fun getUserByUsername(username: String): UserDTO {
        val userOptional = userRepository.findByUsername(username)
        require(userOptional.isPresent) { "User not found" }
        return userOptional.get().toDTO()
    }

    // TODO: Change all services to not require id to find the user,
    //  but rather use email OR accept that front has to know all the ids
    fun updateUser(
        id: Long,
        updateUserDTO: UpdateUserDTO,
    ): UserDTO {
        val userOptional = userRepository.findById(id)
        require(userOptional.isPresent) { "User not found" }
        val user = userOptional.get()

        user.firstName = updateUserDTO.firstName
        user.lastName = updateUserDTO.lastName
        user.username = updateUserDTO.username

        userRepository.save(user)
        return user.toDTO()
    }

    fun updateUserByEmail(
        email: String,
        userDTO: UserDTO,
    ): UserDTO {
        val userOptional = userRepository.findByEmail(email)
        require(userOptional.isPresent) { "User not found" }
        val user = userOptional.get()

        user.firstName = userDTO.firstName
        user.lastName = userDTO.lastName
        user.username = userDTO.username

        userRepository.save(user)
        return user.toDTO()
    }

    fun updateUserEmail(
        id: Long,
        emailUpdateDTO: EmailUpdateDTO,
    ): UserDTO {
        val userOptional = userRepository.findById(id)
        require(userOptional.isPresent) { "User not found" }
        val user = userOptional.get()

        val existingEmailUser = userRepository.findByEmail(emailUpdateDTO.email)
        require(!(existingEmailUser.isPresent && existingEmailUser.get().id != id)) { "Email already in use" }

        user.email = emailUpdateDTO.email
        userRepository.save(user)
        return user.toDTO()
    }

    fun deleteUser(id: Long) {
        require(userRepository.existsById(id)) { "User not found" }
        userRepository.deleteById(id)
    }

    fun getAllUsers(): List<UserDTO> = userRepository.findAll().map { it.toDTO() }

    fun searchUsers(searchUserDTO: SearchUserDTO): List<UserDTO> {
        val users = userRepository.findAll()

        return users
            .filter { user ->
                (
                    searchUserDTO.firstName == null ||
                        user.firstName.contains(searchUserDTO.firstName, ignoreCase = true)
                ) &&
                    (
                        searchUserDTO.lastName == null ||
                            user.lastName.contains(searchUserDTO.lastName, ignoreCase = true)
                    ) &&
                    (
                        searchUserDTO.username == null ||
                            user.username.contains(searchUserDTO.username, ignoreCase = true)
                    ) &&
                    (
                        searchUserDTO.email == null ||
                            user.email.contains(searchUserDTO.email, ignoreCase = true)
                    )
            }.map { it.toDTO() }
    }
}
