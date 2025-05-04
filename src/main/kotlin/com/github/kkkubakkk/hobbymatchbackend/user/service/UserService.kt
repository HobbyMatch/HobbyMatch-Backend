package com.github.kkkubakkk.hobbymatchbackend.user.service

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
    fun findOrCreateOAuthUser(
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
        require(userOptional.isPresent) { "User not found" }
        return userOptional.get()
    }

    fun updateUser(
        id: Long,
        userDto: UpdateUserDTO,
    ): User {
        val userOptional = userRepository.findById(id)
        require(userOptional.isPresent) { "User not found" }

        val user = userOptional.get()
        user.name = userDto.name
        user.email = userDto.email
        user.hobbies.clear()
        user.hobbies =
            userDto.hobbies
                .map { hobbyDto ->
                    val hobbyOptional = hobbyRepository.findByName(hobbyDto.name)
                    require(hobbyOptional.isPresent) { "Hobby not found" }
                    hobbyOptional.get()
                }.toMutableSet()

        return userRepository.save(user)
    }
}
