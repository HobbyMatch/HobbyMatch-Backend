package com.github.kkkubakkk.hobbymatchbackend.utils

import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DbDataInitializer(private val userRepository: UserRepository) : CommandLineRunner {
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
                        passwordHash = "\$2a\$12\$4eH0jXhH5A1pQlV4fMFTqOBUE6dZz0dDRE39PzKMvHcTzKjSHTjNa",
                    ),
                    User(
                        firstName = "Anna",
                        lastName = "Nowak",
                        username = "ania_nowak",
                        email = "anna.nowak@example.com",
                        passwordHash = "\$2a\$12\$A0zY4fRkq62xKhRVyFqXq.uOv9N2hKNqz8oHeR.pZ5BzYI1FIEU4W",
                    ),
                    User(
                        firstName = "Piotr",
                        lastName = "Wiśniewski",
                        username = "piotr_w",
                        email = "piotr.wisniewski@example.com",
                        passwordHash = "\$2a\$12\$D.bHUL03FsCvZeg6Zh2uUuQ2D2zT2.LX8PLwHSP1lqTNTFqfU4hxi",
                    ),
                    User(
                        firstName = "Katarzyna",
                        lastName = "Dąbrowska",
                        username = "kasiaD",
                        email = "katarzyna.dabrowska@example.com",
                        passwordHash = "\$2a\$12\$T04HJKDHPpU6z9Pa1F2M6OLB/YUqUMFZ6RoGfWmLZB5c7X.SuB0H2",
                    ),
                    User(
                        firstName = "Michał",
                        lastName = "Lewandowski",
                        username = "michal_lew",
                        email = "michal.lewandowski@example.com",
                        passwordHash = "\$2a\$12\$p1HhJ3RtM7AklEzq0XUp/uZUBpG3C.CxPKIvAcC5PxZgG7xTjHRyG",
                    ),
                )
            userRepository.saveAll(users)
        }
    }
}
