package com.github.kkkubakkk.hobbymatchbackend.security.config

import com.github.kkkubakkk.hobbymatchbackend.security.handler.OAuth2AuthenticationFailureHandler
import com.github.kkkubakkk.hobbymatchbackend.security.handler.OAuth2AuthenticationSuccessHandler
import com.github.kkkubakkk.hobbymatchbackend.security.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("api/public/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2Login { configurer ->
                configurer
                    .authorizationEndpoint {
                        it.baseUri("/api/oauth2/authorization")
                    }.redirectionEndpoint {
                        it.baseUri("/api/oauth2/callback/*")
                    }.userInfoEndpoint {
                        it.userService(customOAuth2UserService())
                    }.successHandler(oAuth2AuthenticationSuccessHandler())
                    .failureHandler(oAuth2AuthenticationFailureHandler())
            }.exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }

        return http.build()
    }

    @Bean
    fun customOAuth2UserService(): OAuth2UserService<OAuth2UserRequest, OAuth2User> = CustomOAuth2UserService()

    @Bean
    fun oAuth2AuthenticationSuccessHandler(): OAuth2AuthenticationSuccessHandler = OAuth2AuthenticationSuccessHandler()

    @Bean
    fun oAuth2AuthenticationFailureHandler(): OAuth2AuthenticationFailureHandler = OAuth2AuthenticationFailureHandler()
}
