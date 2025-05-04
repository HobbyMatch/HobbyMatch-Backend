package com.github.kkkubakkk.hobbymatchbackend.security.component

import com.github.kkkubakkk.hobbymatchbackend.security.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtils: JwtUtils,
    @Lazy private val userDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val jwt = parseJwt(request)
            if (jwt != null) {
                val username = jwtUtils.getUsernameFromToken(jwt)
                val userDetails = userDetailsService.loadUserByUsername(username)

                if (jwtUtils.validateToken(jwt, userDetails)) {
                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.authorities,
                        )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (e: io.jsonwebtoken.JwtException) {
            logger.error("Invalid JWT token: ${e.message}")
        } catch (e: org.springframework.security.core.userdetails.UsernameNotFoundException) {
            logger.error("User not found: ${e.message}")
        } catch (e: org.springframework.security.core.AuthenticationException) {
            logger.error("Authentication error: ${e.message}")
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: ${e.message}")
        }

        filterChain.doFilter(request, response)
    }

    private fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth = request.getHeader("Authorization")

        return if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            headerAuth.substring(START_INDEX)
        } else {
            null
        }
    }

    companion object {
        private const val START_INDEX = 7
    }
}
