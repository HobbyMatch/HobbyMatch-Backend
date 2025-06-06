package com.github.kkkubakkk.hobbymatchbackend.utils

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.event.repository.EventRepository
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DbDataInitializer(
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
    private val eventRepository: EventRepository,
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(DbDataInitializer::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        logger.info("Starting database initialization...")

        if (hobbyRepository.count() == 0L) {
            hobbyRepository.saveAll(createHobbies())
        }
        if (userRepository.count() == 0L) {
            userRepository.saveAll(createUsers())
        }
        if (eventRepository.count() == 0L) {
            eventRepository.saveAll(createEvents())
        }

        logger.info("Database initialization completed successfully")
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
            name: String,
            email: String,
            hobbyNames: List<String>,
        ): User {
            val user =
                User(
                    name = name,
                    email = email,
                )

            hobbyNames.mapNotNull { hobbiesByName[it] }.forEach { hobby ->
                user.hobbies.add(hobby)
                hobby.users.add(user)
            }

            return user
        }

        return listOf(
            userWithHobbies(
                "Jan Kowalski",
                "jan.kowalski@example.com",
                listOf("Football", "Chess", "Gym"),
            ),
            userWithHobbies(
                "Anna Nowak",
                "anna.nowak@example.com",
                listOf("Tennis", "Swimming"),
            ),
            userWithHobbies(
                "Piotr Wiśniewski",
                "piotr.wisniewski@example.com",
                listOf("Hiking", "Football"),
            ),
            userWithHobbies(
                "Katarzyna Dąbrowska",
                "katarzyna.dabrowska@example.com",
                listOf("Fishing", "Gym", "Running"),
            ),
        )
    }

    private fun createEvents(): List<Event> {
        val users = userRepository.findAll()
        val hobbies = hobbyRepository.findAll().associateBy { it.name }

        val jan = users.find { it.email == "jan.kowalski@example.com" }!!
        val anna = users.find { it.email == "anna.nowak@example.com" }!!
        val piotr = users.find { it.email == "piotr.wisniewski@example.com" }!!

        val location1 = Location(latitude = 52.2297, longitude = 21.0122)
        val location2 = Location(longitude = 50.0647, latitude = 19.9450)
        // Creating Business Clients
        val bclient1 =
            BusinessClient(
                name = "John Doe",
                email = "john_doe@example.com",
                taxId = "123321",
                venues = mutableSetOf(),
            )
        val venue1 =
            Venue(
                name = "Football field",
                description = "4x7 meters outdoor grass field",
                address = "Nowowiejska St. 13",
                location = location1,
                owner = bclient1,
            )
        val venue2 =
            Venue(
                name = "Swimming pool",
                description = "3x21 meters indoor pool",
                address = "Nowogrodzka St. 13B",
                location = location2,
                owner = bclient1,
            )
        bclient1.venues.add(venue1)
        bclient1.venues.add(venue2)

        val location12 = Location(latitude = 52.2297, longitude = 21.0122)
        val location22 = Location(longitude = 50.0647, latitude = 19.9450)
        // Creating Business Clients
        val bclient12 =
            BusinessClient(
                name = "John Doe",
                email = "john_doe@example.com",
                taxId = "12321231",
                venues = mutableSetOf(),
            )
        val venue12 =
            Venue(
                name = "Football field",
                description = "4x7 meters outdoor grass field",
                address = "Nowowiejska St. 13",
                location = location12,
                owner = bclient12,
            )
        val venue22 =
            Venue(
                name = "Swimming pool",
                description = "3x21 meters indoor pool",
                address = "Nowogrodzka St. 13B",
                location = location22,
                owner = bclient12,
            )
        bclient12.venues.add(venue12)
        bclient12.venues.add(venue22)

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
