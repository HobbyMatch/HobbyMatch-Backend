package com.github.kkkubakkk.hobbymatchbackend.utils

import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DbDataInitializer(
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
) : CommandLineRunner {
    @Transactional
    override fun run(vararg args: String?) {
        if (hobbyRepository.count() == 0L) {
            hobbyRepository.saveAll(createHobbies())
        }
        if (userRepository.count() == 0L) {
            userRepository.saveAll(createUsers())
        }
    }

    private fun createHobbies(): List<Hobby> =
        listOf(
            Hobby(name = "Football"),
            Hobby(name = "Chess"),
            Hobby(name = "Basketball"),
            Hobby(name = "Running"),
            Hobby(name = "Gym"),
            Hobby(name = "Swimming"),
            Hobby(name = "Fishing"),
            Hobby(name = "Tennis"),
            Hobby(name = "Hiking"),
        )

    private fun createUsers(): List<User> {
        val hobbiesByName = hobbyRepository.findAll().associateBy { it.name }

        fun userWithHobbies(
            firstName: String,
            lastName: String,
            username: String,
            email: String,
            birthday: String,
            hobbyNames: List<String>,
        ): User {
            val user =
                User(
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    email = email,
                    birthday = LocalDate.parse(birthday),
                )

            hobbyNames.mapNotNull { hobbiesByName[it] }.forEach { hobby ->
                user.hobbies.add(hobby)
                hobby.users.add(user)
            }

            return user
        }

        return listOf(
            userWithHobbies(
                "Jan",
                "Kowalski",
                "janek123",
                "jan.kowalski@example.com",
                "2000-01-01",
                listOf("Football", "Chess", "Gym"),
            ),
            userWithHobbies(
                "Anna",
                "Nowak",
                "ania_nowak",
                "anna.nowak@example.com",
                "1997-10-14",
                listOf("Tennis", "Swimming"),
            ),
            userWithHobbies(
                "Piotr",
                "Wiśniewski",
                "piotr_w",
                "piotr.wisniewski@example.com",
                "1990-06-08",
                listOf("Hiking", "Football"),
            ),
            userWithHobbies(
                "Katarzyna",
                "Dąbrowska",
                "kasiaD",
                "katarzyna.dabrowska@example.com",
                "1989-12-17",
                listOf("Fishing", "Gym", "Running"),
            ),
        )
    }
}
