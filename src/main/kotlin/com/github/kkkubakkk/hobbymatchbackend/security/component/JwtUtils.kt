package com.github.kkkubakkk.hobbymatchbackend.security.component

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.Date
import kotlin.collections.HashMap

@Component
class JwtUtils {
    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.access.expiration}")
    private val accessJwtExpirationMs: Long = 3600000

    @Value("\${jwt.refresh.expiration}")
    private val refreshJwtExpirationMs: Long = 864000000

    fun generateAccessToken(
        userId: Long,
        role: String,
    ): String = generateToken(userId, role, accessJwtExpirationMs)

    fun generateRefreshToken(
        userId: Long,
        role: String,
    ): String = generateToken(userId, role, refreshJwtExpirationMs, true)

    private fun generateToken(
        userId: Long,
        role: String,
        expiration: Long,
        isRefreshToken: Boolean = false,
    ): String {
        val claims = HashMap<String, Any>()
        claims["userId"] = userId
        claims["role"] = role
        if (isRefreshToken) {
            claims["tokenType"] = "refresh"
        } else {
            claims["tokenType"] = "access"
        }

        return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()), SignatureAlgorithm.HS512)
            .compact()
    }

    fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth = request.getHeader("Authorization")

        return if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            headerAuth.substring(START_INDEX)
        } else {
            null
        }
    }

    fun getUserIdFromToken(token: String): Long {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val claims =
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body

        return (claims["userId"] as Number).toLong()
    }

    fun getUserRoleFromToken(token: String): String {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val claims =
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body

        return claims["role"] as String
    }

    fun validateToken(token: String): Boolean {
        try {
            val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
            val claims =
                Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .body

            return !claims.expiration.before(Date())
        } catch (e: Exception) {
            return false
        }
    }

    fun isRefreshToken(token: String): Boolean {
        try {
            val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
            val claims =
                Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .body

            return claims["tokenType"] == "refresh"
        } catch (e: Exception) {
            return false
        }
    }

    companion object {
        private const val START_INDEX = 7

        @JvmStatic
        fun getAuthenticatedUserId(): Long {
            val authentication =
                SecurityContextHolder.getContext().authentication
                    ?: throw SecurityException("Access denied")

            return authentication.principal as? Long
                ?: throw SecurityException("Access denied")
        }
    }
}
