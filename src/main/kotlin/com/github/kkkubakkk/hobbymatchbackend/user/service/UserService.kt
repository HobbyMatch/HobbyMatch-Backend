package com.github.kkkubakkk.hobbymatchbackend.user.service

import com.github.kkkubakkk.hobbymatchbackend.exception.NoAccessException
import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
) {
    fun createUser(
        email: String,
        name: String,
    ): User {
        val userOptional = userRepository.findByEmail(email)
        if (userOptional.isPresent) {
            return userOptional.get()
        }

        val user =
            User(
                name = name,
                email = email,
            )

        return userRepository.save(user)
    }

    fun getUser(id: Long): User {
        val userOptional = userRepository.findById(id)
        if (userOptional.isEmpty) {
            throw RecordNotFoundException("User not found")
        }
        return userOptional.get()
    }

    fun updateUser(
        id: Long,
        userDto: UpdateUserDTO,
    ): User {
        val user = getUser(id)
        user.name = userDto.name
        user.email = userDto.email
        user.hobbies.forEach { hobby ->
            hobby.users.remove(user)
        }
        user.hobbies.clear()
        user.hobbies =
            userDto.hobbies
                .map { hobbyDto ->
                    val hobbyOptional = hobbyRepository.findByName(hobbyDto.name)
                    if (hobbyOptional.isEmpty) {
                        throw RecordNotFoundException("Hobby not found")
                    }
                    hobbyOptional.get()
                }.toMutableSet()
        user.hobbies.forEach { hobby ->
            hobby.users.add(user)
        }

        return userRepository.save(user)
    }

    fun verifyUserAccess(
        userId: Long,
        authUserId: Long,
    ) {
        if (authUserId != userId) {
            throw NoAccessException(
                "\"User ID mismatch: Authenticated user ID: $authUserId, Requested user ID: $userId\"",
            )
        }
    }
}
