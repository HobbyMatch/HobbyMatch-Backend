package com.github.kkkubakkk.hobbymatchbackend.security.controller

import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import io.jsonwebtoken.io.IOException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.GeneralSecurityException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtUtils: JwtUtils,
    private val userService: UserService,
) {
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private lateinit var googleClientId: String

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication
        // Return user information
        return ResponseEntity.ok(mapOf("username" to authentication.name))
    }

    @PostMapping("/refresh")
    fun refreshToken(): ResponseEntity<Any> {
        val authentication = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val newToken = jwtUtils.generateJwtToken(authentication)
        return ResponseEntity.ok(mapOf("token" to newToken))
    }

    @PostMapping("/mobile/google")
    fun authenticateWithGoogle(
        @RequestBody request: GoogleTokenRequest,
    ): ResponseEntity<AuthResponse> {
        try {
            val verifier =
                GoogleIdTokenVerifier
                    .Builder(NetHttpTransport(), GsonFactory())
                    .setAudience(listOf(googleClientId))
                    .build()

            val idToken = verifier.verify(request.idToken)
            if (idToken != null) {
                val payload = idToken.payload
                val email = payload["email"] as String

                // Get the full name from Google profile
                val firstName = payload["given_name"] as? String ?: ""
                val lastName = payload["family_name"] as? String ?: ""

                // Find or create user based on Google email
                val user = userService.findOrCreateOAuthUser(email, firstName, lastName)

                // Generate JWT token
                val token = convertUserToJwt(user)

                // Return token and user info
                val userInfo =
                    UserInfo(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        email = user.email,
                    )

                return ResponseEntity.ok(AuthResponse(token, userInfo))
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: GeneralSecurityException) {
            // Log the exception
            println("VerificationException: ${e.message}")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: IOException) {
            // Log the exception
            println("Exception: ${e.message}")
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    private fun convertUserToJwt(user: User): String {
        val userDetails =
            org.springframework.security.core.userdetails.User
                .withUsername(user.email)
                .password("") // No password for OAuth users
                .authorities("ROLE_USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build()

        return jwtUtils.generateJwtToken(userDetails)
    }
}

data class GoogleTokenRequest(
    val idToken: String,
)

data class AuthResponse(
    val token: String,
    val user: UserInfo,
)

data class UserInfo(
    val email: String,
    val firstName: String,
    val lastName: String,
)
