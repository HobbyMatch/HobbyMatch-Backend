package com.github.kkkubakkk.hobbymatchbackend.user.service

import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.test.context.bean.override.mockito.MockitoBean

class UserServiceTest {
    @MockitoBean
    private lateinit var userRepository: UserRepository
}
