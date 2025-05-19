package com.github.kkkubakkk.hobbymatchbackend.security.controller

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import io.jsonwebtoken.io.IOException
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.test.util.ReflectionTestUtils
import java.security.GeneralSecurityException

class AuthControllerUnitTest {
    private lateinit var authController: AuthController
    private lateinit var jwtUtils: JwtUtils
    private lateinit var userService: UserService
    private lateinit var businessClientService: BusinessClientService

    private val user =
        User(
            id = 1,
            name = "John Doe",
            email = "john.doe@example.com",
        )

    private val userDTO =
        UserDTO(
            id = 1,
            name = "John Doe",
            email = "john.doe@example.com",
            hobbies = emptyList(),
        )

    private val businessClient =
        BusinessClient(
            id = 1,
            name = "Business Name",
            email = "business@example.com",
            taxId = "12345",
        )

    @BeforeEach
    fun setup() {
        jwtUtils = mock(JwtUtils::class.java)
        userService = mock(UserService::class.java)
        businessClientService = mock(BusinessClientService::class.java)
        authController = AuthController(jwtUtils, userService, businessClientService)

        mockkObject(JwtUtils.Companion)
        every { JwtUtils.getAuthenticatedUserId() } returns 1L
    }

    @Test
    fun `getCurrentUser returns user data when authenticated`() {
        // Given
        given(userService.getUser(1L)).willReturn(user)

        // When
        val response = authController.getCurrentUser()

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(userDTO, response.body)
        verify(userService).getUser(1L)
    }

    @Test
    fun `getCurrentUser returns error when exception occurs`() {
        // Given
        given(userService.getUser(1L)).willThrow(RuntimeException("Test exception"))

        // When
        val response = authController.getCurrentUser()

        // Then
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun `refreshToken returns new tokens when refresh token is valid`() {
        // Given
        val request = RefreshTokenRequest("valid_refresh_token")
        given(jwtUtils.validateToken(request.refreshToken)).willReturn(true)
        given(jwtUtils.isRefreshToken(request.refreshToken)).willReturn(true)
        given(jwtUtils.getUserIdFromToken(request.refreshToken)).willReturn(1L)
        given(jwtUtils.getUserRoleFromToken(request.refreshToken)).willReturn("USER")
        given(jwtUtils.generateAccessToken(1L, "USER")).willReturn("new_access_token")

        // When
        val response = authController.refreshToken(request)

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals("new_access_token", response.body?.accessToken)
        Assertions.assertEquals("valid_refresh_token", response.body?.refreshToken)
    }

    @Test
    fun `refreshToken returns 401 when refresh token is invalid`() {
        // Given
        val request = RefreshTokenRequest("invalid_token")
        given(jwtUtils.validateToken(request.refreshToken)).willReturn(false)

        // When
        val response = authController.refreshToken(request)

        // Then
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `refreshToken returns 401 when not a refresh token`() {
        // Given
        val request = RefreshTokenRequest("access_token")
        given(jwtUtils.validateToken(request.refreshToken)).willReturn(true)
        given(jwtUtils.isRefreshToken(request.refreshToken)).willReturn(false)

        // When
        val response = authController.refreshToken(request)

        // Then
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `authenticateWithGoogle creates user when role is USER`() {
        // Mock the GoogleIdTokenVerifier and GoogleIdToken classes
        val mockPayload = mock(GoogleIdToken.Payload::class.java)
        given(mockPayload["email"]).willReturn("test@example.com")
        given(mockPayload["given_name"]).willReturn("Test")
        given(mockPayload["family_name"]).willReturn("User")

        val mockIdToken = mock(GoogleIdToken::class.java)
        given(mockIdToken.payload).willReturn(mockPayload)

        // Use MockK to mock the constructor and verify method
        mockkConstructor(GoogleIdTokenVerifier::class)
        every { anyConstructed<GoogleIdTokenVerifier>().verify(any<String>()) } returns mockIdToken

        // Set up other required mocks
        ReflectionTestUtils.setField(authController, "googleClientId", "test-client-id")
        val request = GoogleTokenRequest("valid_id_token", "USER")
        given(userService.createUser("test@example.com", "Test User")).willReturn(user)
        given(jwtUtils.generateAccessToken(1L, "USER")).willReturn("access_token")
        given(jwtUtils.generateRefreshToken(1L, "USER")).willReturn("refresh_token")

        // When
        val response = authController.authenticateWithGoogle(request)

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        verify(userService).createUser("test@example.com", "Test User")
    }

    @Test
    fun `authenticateWithGoogle creates business client when role is BUSINESS`() {
        // Mock the GoogleIdTokenVerifier and GoogleIdToken classes
        val mockPayload = mock(GoogleIdToken.Payload::class.java)
        given(mockPayload["email"]).willReturn("test@example.com")
        given(mockPayload["given_name"]).willReturn("Test")
        given(mockPayload["family_name"]).willReturn("User")

        val mockIdToken = mock(GoogleIdToken::class.java)
        given(mockIdToken.payload).willReturn(mockPayload)

        // Use MockK to mock the constructor
        mockkConstructor(GoogleIdTokenVerifier::class)
        every { anyConstructed<GoogleIdTokenVerifier>().verify(any<String>()) } returns mockIdToken

        // Set up other required mocks
        ReflectionTestUtils.setField(authController, "googleClientId", "test-client-id")
        val request = GoogleTokenRequest("valid_id_token", "BUSINESS")
        given(businessClientService.createBusinessClient(anyString(), anyString(), anyString()))
            .willReturn(businessClient)
        given(jwtUtils.generateAccessToken(1L, "BUSINESS")).willReturn("access_token")
        given(jwtUtils.generateRefreshToken(1L, "BUSINESS")).willReturn("refresh_token")

        // When
        val response = authController.authenticateWithGoogle(request)

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        verify(businessClientService).createBusinessClient(anyString(), anyString(), anyString())
    }

    @Test
    fun `authenticateWithGoogle returns 401 when token verification fails`() {
        // Given
        ReflectionTestUtils.setField(authController, "googleClientId", "test-client-id")

        // Use MockK to mock the constructor
        mockkConstructor(GoogleIdTokenVerifier::class)
        every { anyConstructed<GoogleIdTokenVerifier>().verify(any<String>()) } returns null

        val request = GoogleTokenRequest("invalid_token", "USER")

        // When
        val response = authController.authenticateWithGoogle(request)

        // Then
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `authenticateWithGoogle handles security exception`() {
        // Given
        ReflectionTestUtils.setField(authController, "googleClientId", "test-client-id")

        // Use MockK
        mockkConstructor(GoogleIdTokenVerifier::class)
        every {
            anyConstructed<GoogleIdTokenVerifier>().verify(any<String>())
        } throws GeneralSecurityException("Security error")

        val request = GoogleTokenRequest("invalid_token", "USER")

        // When
        val response = authController.authenticateWithGoogle(request)

        // Then
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `authenticateWithGoogle handles IO exception`() {
        // Given
        ReflectionTestUtils.setField(authController, "googleClientId", "test-client-id")

        // Use MockK to mock the constructor
        mockkConstructor(GoogleIdTokenVerifier::class)
        every { anyConstructed<GoogleIdTokenVerifier>().verify(any<String>()) } throws IOException("Network error")

        val request = GoogleTokenRequest("invalid_token", "USER")

        // When
        val response = authController.authenticateWithGoogle(request)

        // Then
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }
}
