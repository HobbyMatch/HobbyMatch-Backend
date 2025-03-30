package com.github.kkkubakkk.hobbymatchbackend.security.service

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = super.loadUser(userRequest)

        // TODO: Implement custom logic to save or update the user in the database (instructions on github)

        return user
    }
}
