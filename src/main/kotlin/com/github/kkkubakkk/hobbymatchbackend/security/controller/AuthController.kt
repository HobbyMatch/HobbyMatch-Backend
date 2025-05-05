package com.github.kkkubakkk.hobbymatchbackend.security.controller

import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils
import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils.Companion.getAuthenticatedUserId
import com.github.kkkubakkk.hobbymatchbackend.security.dto.LoginDTO
import com.github.kkkubakkk.hobbymatchbackend.security.dto.toLoginDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import io.jsonwebtoken.io.IOException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.GeneralSecurityException

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtUtils: JwtUtils,
    private val userService: UserService,
    private val businessClientService: BusinessClientService,
) {
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private lateinit var googleClientId: String

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserDTO> {
        try {
            val authUserId = getAuthenticatedUserId()
            val user = userService.getUser(authUserId)
            return ResponseEntity.ok(user.toDTO())
        } catch (e: Exception) {
            println(e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequest,
    ): ResponseEntity<TokenResponse> {
        if (!jwtUtils.validateToken(request.refreshToken) || !jwtUtils.isRefreshToken(request.refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        try {
            val userId = jwtUtils.getUserIdFromToken(request.refreshToken)
            val role = jwtUtils.getUserRoleFromToken(request.refreshToken)

            val newAccessToken = jwtUtils.generateAccessToken(userId, role)

            return ResponseEntity.ok(TokenResponse(newAccessToken, request.refreshToken))
        } catch (ex: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("/login/google")
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

                val firstName = payload["given_name"] as? String ?: ""
                val lastName = payload["family_name"] as? String ?: ""
                val name = "$firstName $lastName"

                var loginDto = LoginDTO()
                if (request.role == "USER") {
                    val user = userService.createUser(email, name)
                    loginDto = user.toLoginDTO()
                } else {
                    val bClient = businessClientService.createBusinessClient(email, name)
                    loginDto = bClient.toLoginDTO()
                }

                val accessToken = jwtUtils.generateAccessToken(loginDto.id, request.role)
                val refreshToken = jwtUtils.generateRefreshToken(loginDto.id, request.role)

                return ResponseEntity.ok(AuthResponse(accessToken, refreshToken, loginDto))
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: GeneralSecurityException) {
            println("VerificationException: ${e.message}")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: IOException) {
            println("Exception: ${e.message}")
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}

data class GoogleTokenRequest(
    val idToken: String,
    val role: String,
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val loginInfo: LoginDTO,
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshTokenRequest(
    val refreshToken: String,
)
