package com.github.kkkubakkk.hobbymatchbackend.event.service

import com.github.kkkubakkk.hobbymatchbackend.event.dto.CreateOrUpdateEventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.EventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.event.repository.EventRepository
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

    fun getEvent(eventId: Long): EventDTO {
        val event = eventRepository.findById(eventId)
        if (event.isPresent) {
            return event.get().toDTO()
        } else {
            throw RuntimeException("Event with id $eventId does not exist")
        }
    }

    @Transactional
    fun updateEvent(
        id: Long,
        createOrUpdateEventDTO: CreateOrUpdateEventDTO,
    ): EventDTO {
        val event = eventRepository.findById(id)
        require(event.isPresent) { "Event with id $id not found" }

        val eventEntity = event.get()
        eventEntity.title = createOrUpdateEventDTO.title
        eventEntity.description = createOrUpdateEventDTO.description
        eventEntity.location =
            Location(
                longitude = createOrUpdateEventDTO.location.longitude,
                latitude = createOrUpdateEventDTO.location.latitude,
            )
        eventEntity.startTime = LocalDateTime.parse(createOrUpdateEventDTO.startTime)
        eventEntity.endTime = LocalDateTime.parse(createOrUpdateEventDTO.endTime)
        eventEntity.price = createOrUpdateEventDTO.price
        eventEntity.minUsers = createOrUpdateEventDTO.minUsers
        eventEntity.maxUsers = createOrUpdateEventDTO.maxUsers

        // Update hobbies
        val newHobbies = hobbyRepository.findAllByNameIn(createOrUpdateEventDTO.hobbies.map { it.name })

        // Remove event from old hobbies that aren't in the new set
        val hobbiesToRemove = eventEntity.hobbies.filter { !newHobbies.contains(it) }
        for (hobby in hobbiesToRemove) {
            hobby.events.remove(eventEntity)
        }

        // Add event to new hobbies
        eventEntity.hobbies.clear()
        eventEntity.hobbies.addAll(newHobbies)

        for (hobby in newHobbies) {
            if (!hobby.events.contains(eventEntity)) {
                hobby.events.add(eventEntity)
            }
        }

        return eventRepository.save(eventEntity).toDTO()
    }

    @Transactional
    fun deleteEvent(id: Long) {
        val event = eventRepository.findById(id)
        require(event.isPresent) { "Event with id $id not found" }

        val eventEntity = event.get()

        // Remove from participants
        eventEntity.participants.forEach {
            it.participatedEvents.remove(eventEntity)
        }
        // Remove from hobbies
        eventEntity.hobbies.forEach {
            it.events.remove(eventEntity)
        }
        // Remove from organizer
        eventEntity.organizer.organizedEvents.remove(eventEntity)
        // Delete the activity
        eventRepository.delete(eventEntity)
    }

    fun enrollInEvent(
        eventId: Long,
        participantId: Long,
    ): EventDTO {
        val event = eventRepository.findById(eventId)
        require(event.isPresent) { "Event with id $eventId not found" }

        val participant = userRepository.findById(participantId).get()

        event.get().participants.add(participant)
        participant.participatedEvents.add(event.get())

        eventRepository.save(event.get())
        userRepository.save(participant)

        return event.get().toDTO()
    }

    fun withdrawFromEvent(
        eventId: Long,
        participantId: Long,
    ): EventDTO {
        val event = eventRepository.findById(eventId)
        require(event.isPresent) { "Event with id $eventId not found" }

        val participant = userRepository.findById(participantId).get()

        event.get().participants.remove(participant)
        participant.participatedEvents.remove(event.get())

        eventRepository.save(event.get())
        userRepository.save(participant)

        return event.get().toDTO()
    }
}
