package com.github.kkkubakkk.hobbymatchbackend.user.service

import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toEntity
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (private val userRepository: UserRepository){
    fun createUser():UserDTO{
        //TODO: implement
    }
    fun getUserById(id:Long): UserDTO {
        // TODO: verify
        val user = userRepository.findById(id).orElseThrow{
            IllegalArgumentException("User not found")
        }
        return user.toDTO()
    }
    fun updateUser(id:Long, userDTO: UserDTO): UserDTO {
        // TODO: implement
        //  finding the user in the database
        //  and changing its values to those of userDTO
        return userDTO// TODO: change, dummy answer
    }
    fun deleteUser(id:Long) {
        //TODO: implement deleting a user from the database
        // by its id
        userRepository.deleteById(id)
    }
    fun getAllUsers():List<UserDTO> {
        return  emptyList(); // TODO: implement
    }
}