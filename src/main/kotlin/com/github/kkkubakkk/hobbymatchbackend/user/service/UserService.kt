package com.github.kkkubakkk.hobbymatchbackend.user.service

import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.user.dto.CreateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.EmailUpdateDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.SearchUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class UserService(
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
) {
    fun createUser(createUserDTO: CreateUserDTO): UserDTO {
        val existingUser = userRepository.findByEmail(createUserDTO.email)
        require(!existingUser.isPresent) { "User with this email already exists" }

        val hobbies = hobbyRepository.findAllById(createUserDTO.hobbiesId)
        require(hobbies.size == createUserDTO.hobbiesId.size) { "Some specified hobbies do not exist" }

        val user =
            User(
                firstName = createUserDTO.firstName,
                lastName = createUserDTO.lastName,
                username = createUserDTO.username,
                email = createUserDTO.email,
                hobbies = hobbies.toMutableSet(),
                birthday = LocalDate.parse(createUserDTO.birthday, DateTimeFormatter.ISO_LOCAL_DATE),
                bio = createUserDTO.bio,
            )

        for (hobby in hobbies) {
            hobby.users.add(user)
        }

        userRepository.save(user)

        return user.toDTO()
    }

    fun userExists(email: String): Boolean = userRepository.findByEmail(email).isPresent

    fun getUserById(id: Long): UserDTO {
        val userOptional = userRepository.findById(id)
        require(userOptional.isPresent) { "User not found" }
        return userOptional.get().toDTO()
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

    fun updateUser(
        id: Long,
        updateUserDTO: UpdateUserDTO,
    ): UserDTO {
        val userOptional = userRepository.findById(id)
        require(userOptional.isPresent) { "User not found" }
        val user = userOptional.get()

        val newHobbies = hobbyRepository.findAllById(updateUserDTO.hobbiesId)
        require(newHobbies.size == updateUserDTO.hobbiesId.size) { "Some specified hobbies do not exist" }

        updateHobbies(user, newHobbies)

        user.firstName = updateUserDTO.firstName
        user.lastName = updateUserDTO.lastName
        user.username = updateUserDTO.username
        user.hobbies = newHobbies.toMutableSet()
        user.bio = updateUserDTO.bio

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

        val newHobbies = hobbyRepository.findAllById(userDTO.hobbiesId)
        require(newHobbies.size == userDTO.hobbiesId.size) { "Some specified hobbies do not exist" }

        updateHobbies(user, newHobbies)

        user.firstName = userDTO.firstName
        user.lastName = userDTO.lastName
        user.username = userDTO.username
        user.hobbies = newHobbies.toMutableSet()
        user.bio = userDTO.bio

        userRepository.save(user)
        return user.toDTO()
    }

    fun updateHobbies(
        user: User,
        newHobbies: List<Hobby>,
    ) {
        val hobbiesToRemove = user.hobbies.filter { it !in newHobbies }
        hobbiesToRemove.forEach { hobby ->
            hobby.users.remove(user)
        }

        val hobbiesToAdd = newHobbies.filter { it !in user.hobbies }
        hobbiesToAdd.forEach { hobby ->
            hobby.users.add(user)
        }
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
