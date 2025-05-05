package com.github.kkkubakkk.hobbymatchbackend.security.component

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtils: JwtUtils,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val jwt = jwtUtils.parseJwt(request)
            if (jwt != null && jwtUtils.validateToken(jwt) && !jwtUtils.isRefreshToken(jwt)) {
                val userId = jwtUtils.getUserIdFromToken(jwt)
                val userRole = jwtUtils.getUserRoleFromToken(jwt)

                val authorities = listOf(SimpleGrantedAuthority("ROLE_$userRole"))
                val authentication = UsernamePasswordAuthenticationToken(userId, jwt, authorities)

                SecurityContextHolder.getContext().authentication = authentication
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
}
