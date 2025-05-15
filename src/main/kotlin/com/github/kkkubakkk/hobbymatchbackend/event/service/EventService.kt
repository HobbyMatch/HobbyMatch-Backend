package com.github.kkkubakkk.hobbymatchbackend.event.service

import com.github.kkkubakkk.hobbymatchbackend.event.dto.CreateOrUpdateEventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.EventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.event.repository.EventRepository
import com.github.kkkubakkk.hobbymatchbackend.exception.NoAccessException
import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
) {
    fun createEvent(
        createOrUpdateEventDTO: CreateOrUpdateEventDTO,
        organizerId: Long,
    ): EventDTO {
        val organizer = userRepository.findById(organizerId).get()
        val hobbies = hobbyRepository.findAllByNameIn(createOrUpdateEventDTO.hobbies.map { it.name })
        val event =
            Event(
                organizer = organizer,
                title = createOrUpdateEventDTO.title,
                description = createOrUpdateEventDTO.description,
                location =
                    Location(
                        longitude = createOrUpdateEventDTO.location.longitude,
                        latitude = createOrUpdateEventDTO.location.latitude,
                    ),
                startTime = LocalDateTime.parse(createOrUpdateEventDTO.startTime),
                endTime = LocalDateTime.parse(createOrUpdateEventDTO.endTime),
                hobbies = hobbies.toMutableSet(),
                price = createOrUpdateEventDTO.price,
                minUsers = createOrUpdateEventDTO.minUsers,
                maxUsers = createOrUpdateEventDTO.maxUsers,
            )

        organizer.organizedEvents.add(event)
        for (hobby in hobbies) {
            hobby.events.add(event)
        }
        eventRepository.save(event)

        return event.toDTO()
    }

    fun getAllEvents(): List<EventDTO> = eventRepository.findAll().map { it.toDTO() }

    fun getEvent(eventId: Long): Event {
        val event = eventRepository.findById(eventId)
        if (event.isEmpty) {
            throw RecordNotFoundException("Event with id $eventId not found")
        }
        return event.get()
    }

    @Transactional
    fun updateEvent(
        id: Long,
        authUserId: Long,
        createOrUpdateEventDTO: CreateOrUpdateEventDTO,
    ): EventDTO {
        val event = getEvent(id)
        verifyOrganizerAccess(event.organizer.id, authUserId)
        event.title = createOrUpdateEventDTO.title
        event.description = createOrUpdateEventDTO.description
        event.location =
            Location(
                longitude = createOrUpdateEventDTO.location.longitude,
                latitude = createOrUpdateEventDTO.location.latitude,
            )
        event.startTime = LocalDateTime.parse(createOrUpdateEventDTO.startTime)
        event.endTime = LocalDateTime.parse(createOrUpdateEventDTO.endTime)
        event.price = createOrUpdateEventDTO.price
        event.minUsers = createOrUpdateEventDTO.minUsers
        event.maxUsers = createOrUpdateEventDTO.maxUsers

        // Update hobbies
        val newHobbies = hobbyRepository.findAllByNameIn(createOrUpdateEventDTO.hobbies.map { it.name })

        // Remove event from old hobbies that aren't in the new set
        val hobbiesToRemove = event.hobbies.filter { !newHobbies.contains(it) }
        for (hobby in hobbiesToRemove) {
            hobby.events.remove(event)
        }

        // Add event to new hobbies
        event.hobbies.clear()
        event.hobbies.addAll(newHobbies)

        for (hobby in newHobbies) {
            if (!hobby.events.contains(event)) {
                hobby.events.add(event)
            }
        }

        return eventRepository.save(event).toDTO()
    }

    @Transactional
    fun deleteEvent(
        id: Long,
        authUserId: Long,
    ) {
        val event = getEvent(id)
        val organizerId = event.organizer.id
        verifyOrganizerAccess(authUserId, organizerId)
        // Remove from participants
        event.participants.forEach {
            it.participatedEvents.remove(event)
        }
        // Remove from hobbies
        event.hobbies.forEach {
            it.events.remove(event)
        }
        // Remove from organizer
        event.organizer.organizedEvents.remove(event)
        // Delete the activity
        eventRepository.delete(event)
    }

    fun enrollInEvent(
        eventId: Long,
        participantId: Long,
    ): EventDTO {
        val event = getEvent(eventId)
        val participant = userRepository.findById(participantId).get()

        event.participants.add(participant)
        participant.participatedEvents.add(event)

        userRepository.save(participant)

        return eventRepository.save(event).toDTO()
    }

    fun withdrawFromEvent(
        eventId: Long,
        participantId: Long,
    ): EventDTO {
        val event = getEvent(eventId)

        val participant = userRepository.findById(participantId).get()
        event.participants.remove(participant)
        participant.participatedEvents.remove(event)

        userRepository.save(participant)

        return eventRepository.save(event).toDTO()
    }

    fun verifyOrganizerAccess(
        organizerId: Long,
        authUserId: Long,
    ) {
        if (organizerId != authUserId) {
            throw NoAccessException(
                "Access denied for user with id $authUserId, expected the organizer with id $organizerId",
            )
        }
    }
}
