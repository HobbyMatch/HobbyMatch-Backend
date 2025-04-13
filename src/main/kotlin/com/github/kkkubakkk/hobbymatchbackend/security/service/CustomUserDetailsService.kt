package com.github.kkkubakkk.hobbymatchbackend.security.service

import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val tuser = userRepository.findByEmail(username)
        require(tuser.isPresent) { "User not found" }
        val user = tuser.get()

        return org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password("") // Empty string for OAuth users
            .authorities("ROLE_USER")
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }
}
