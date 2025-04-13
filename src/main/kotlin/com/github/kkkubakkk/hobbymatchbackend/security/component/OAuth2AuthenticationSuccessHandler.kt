package com.github.kkkubakkk.hobbymatchbackend.security.component

import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtUtils: JwtUtils,
    private val userService: UserService,
) : SimpleUrlAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val oauthToken = authentication as OAuth2AuthenticationToken
        val oauth2User = oauthToken.principal as OAuth2User

        // Extract user information from OAuth2User
        val email = oauth2User.attributes["email"] as String
        val firstName =
            oauth2User.attributes["given_name"] as? String
                ?: oauth2User.attributes["name"]
                    ?.toString()
                    ?.split(" ")
                    ?.firstOrNull() ?: ""
        val lastName =
            oauth2User.attributes["family_name"] as? String
                ?: oauth2User.attributes["name"]
                    ?.toString()
                    ?.split(" ")
                    ?.drop(1)
                    ?.joinToString(" ") ?: ""

        // Create or update user in your system
        val user = userService.findOrCreateOAuthUser(email, firstName, lastName)

        // Generate JWT token
        val jwtToken = generateJwtToken(user)

        // Redirect to mobile app with token
        // You can use a deep link or a redirect URL that your mobile app can intercept
        val redirectUrl = "yourapp://oauth2/success?token=$jwtToken"
        response.sendRedirect(redirectUrl)
    }

    // Helper method to adapt User to UserDetails
    private fun generateJwtToken(user: User): String {
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
