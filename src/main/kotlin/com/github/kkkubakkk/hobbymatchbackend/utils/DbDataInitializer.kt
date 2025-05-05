package com.github.kkkubakkk.hobbymatchbackend.utils

import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.event.repository.EventRepository
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DbDataInitializer(
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
    private val eventRepository: EventRepository,
) : CommandLineRunner {
    @Transactional
    override fun run(vararg args: String?) {
        if (hobbyRepository.count() == 0L) {
            hobbyRepository.saveAll(createHobbies())
        }
        if (userRepository.count() == 0L) {
            userRepository.saveAll(createUsers())
        }
        if (eventRepository.count() == 0L) {
            eventRepository.saveAll(createEvents())
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

    private fun createEvents(): List<Event> {
        val users = userRepository.findAll()
        val hobbies = hobbyRepository.findAll().associateBy { it.name }

        val jan = users.find { it.username == "janek123" }!!
        val anna = users.find { it.username == "ania_nowak" }!!
        val piotr = users.find { it.username == "piotr_w" }!!

        return listOf(
            Event(
                organizer = jan,
                title = "Football match",
                description = "Casual football game in the city park",
                location = Location(latitude = 52.2297, longitude = 21.0122),
                startTime = LocalDateTime.now().plusDays(2),
                endTime = LocalDateTime.now().plusDays(3),
                price = 15.0,
                minUsers = 20,
                maxUsers = 35,
            ).apply {
                hobbies["Football"]?.let { this.hobbies.add(it) }
                participants.add(jan)
                participants.add(piotr)
            },
            Event(
                organizer = anna,
                title = "Chess competition",
                description = "Monthly chess tournament at the local club",
                location = Location(longitude = 50.0647, latitude = 19.9450),
                startTime = LocalDateTime.now().plusDays(5),
                endTime = LocalDateTime.now().plusDays(6),
                price = 0.0,
                minUsers = 10,
                maxUsers = 50,
            ).apply {
                hobbies["Chess"]?.let { this.hobbies.add(it) }
                participants.add(anna)
                participants.add(jan)
            },
        )
    }
}
