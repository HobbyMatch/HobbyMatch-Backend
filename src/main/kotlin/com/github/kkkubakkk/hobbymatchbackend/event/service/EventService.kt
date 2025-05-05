package com.github.kkkubakkk.hobbymatchbackend.event.service

import com.github.kkkubakkk.hobbymatchbackend.event.dto.CreateOrUpdateEventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.EventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.event.repository.EventRepository
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import com.github.kkkubakkk.hobbymatchbackend.venue.repository.VenueRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
    private val venuesRepository: VenueRepository,
) {
    fun createEvent(
        createOrUpdateEventDTO: CreateOrUpdateEventDTO,
        organizerId: Long,
    ): EventDTO {
        val organizer = userRepository.findById(organizerId).get()
        val hobbies = hobbyRepository.findAllByNameIn(createOrUpdateEventDTO.hobbies.map { it.name })
        val host = venuesRepository.findByLocation(createOrUpdateEventDTO.location)
        require(host.isPresent) { "Host venue not found" }
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
                host = host.get(),
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
        require(event.isPresent) { "Event not found" }
        return event.get().toDTO()
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

        val host = venuesRepository.findByLocation(eventEntity.location)
        require(host.isPresent) { "Host venue not found" }
        // Remove from venue
        host.get().hostedEvents.remove(eventEntity)
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
