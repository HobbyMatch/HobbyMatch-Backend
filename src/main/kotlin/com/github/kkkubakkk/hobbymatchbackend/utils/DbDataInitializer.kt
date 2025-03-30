package com.github.kkkubakkk.hobbymatchbackend.utils

import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DbDataInitializer(
    private val userRepository: UserRepository,
) : CommandLineRunner {
    @Transactional
    override fun run(vararg args: String?) {
        if (userRepository.count() == 0L) { // If users table is empty
            val users =
                listOf(
                    User(
                        firstName = "Jan",
                        lastName = "Kowalski",
                        username = "janek123",
                        email = "jan.kowalski@example.com",
                    ),
                    User(
                        firstName = "Anna",
                        lastName = "Nowak",
                        username = "ania_nowak",
                        email = "anna.nowak@example.com",
                    ),
                    User(
                        firstName = "Piotr",
                        lastName = "Wiśniewski",
                        username = "piotr_w",
                        email = "piotr.wisniewski@example.com",
                    ),
                    User(
                        firstName = "Katarzyna",
                        lastName = "Dąbrowska",
                        username = "kasiaD",
                        email = "katarzyna.dabrowska@example.com",
                    ),
                    User(
                        firstName = "Michał",
                        lastName = "Lewandowski",
                        username = "michal_lew",
                        email = "michal.lewandowski@example.com",
                    ),
                )
            userRepository.saveAll(users)
        }
    }
}
