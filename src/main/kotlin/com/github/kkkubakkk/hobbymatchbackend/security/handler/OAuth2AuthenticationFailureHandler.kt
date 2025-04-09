package com.github.kkkubakkk.hobbymatchbackend.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        // TODO: Implement custom logic to handle failed OAuth2 authentication
        // 1. Handle the failure
        // 2. Redirect the user to the frontend application with an error message
    }
}
