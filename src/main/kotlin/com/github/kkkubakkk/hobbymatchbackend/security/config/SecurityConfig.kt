package com.github.kkkubakkk.hobbymatchbackend.security.config

import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // DEBUG ONLY: Remove authentication of all the endpoints
                    .anyRequest()
                    .permitAll()
                // PRODUCTION: Set up authentication for the endpoints
//                    .requestMatchers("/api/v1/auth/**")
//                    .permitAll()
//                    .requestMatchers("/api/v1/users/**")
//                    .hasAnyRole("USER")
//                    .requestMatchers("/api/v1/business/**")
//                    .hasAnyRole("BUSINESS")
//                    .anyRequest()
//                    .authenticated()
            }.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
