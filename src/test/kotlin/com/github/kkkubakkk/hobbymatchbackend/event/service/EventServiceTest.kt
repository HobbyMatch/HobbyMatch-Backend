
package com.github.kkkubakkk.hobbymatchbackend.event.service

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.event.dto.CreateOrUpdateEventDTO
import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.event.repository.EventRepository
import com.github.kkkubakkk.hobbymatchbackend.exception.RecordNotFoundException
import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.location.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.verify
import java.time.LocalDateTime
import java.util.Optional

class EventServiceTest {
    private val eventRepository: EventRepository = mock()
    private val userRepository: UserRepository = mock()
    private val hobbyRepository: HobbyRepository = mock()
    private val eventService: EventService = EventService(eventRepository, userRepository, hobbyRepository)

    private val eventOrganizer = User(id = 1L, name = "mark", email = "mark@example.com")
    private val participant = User(id = 2L, name = "alice", email = "alice@example.com")
    private val hobby = Hobby(id = 1L, name = "Chess")
    private val location = Location(latitude = 50.0, longitude = 20.0)
    private val now = LocalDateTime.now()
    private val venueOwner = BusinessClient(id = 1L, name = "john", email = "john@example.com", taxId = "111111111")
    private val venue =
        Venue(
            id = 1L,
            name = "arena1",
            description = "big arena",
            address = "Mokotowska 4",
            location = location,
            owner = venueOwner,
        )

    @Test
    fun `create event should save and return event`() {
        // Given
        val dto =
            CreateOrUpdateEventDTO(
                title = "Chess Tournament",
                description = "Big event",
                location = location.toDTO(),
                startTime = now.toString(),
                endTime = now.plusHours(2).toString(),
                price = 20.0,
                minUsers = 5,
                maxUsers = 20,
                hobbies = listOf(hobby.toDTO()),
            )
        given(userRepository.findById(eventOrganizer.id)).willReturn(Optional.of(eventOrganizer))
        given(hobbyRepository.findAllByNameIn(listOf("Chess"))).willReturn(listOf(hobby))

        // When
        val result = eventService.createEvent(dto, eventOrganizer.id)

        // Then
        verify(eventRepository).save(any())
        verify(userRepository).findById(eventOrganizer.id)
        verify(hobbyRepository).findAllByNameIn(listOf("Chess"))
        Assertions.assertEquals(dto.title, result.title)
    }

    @Test
    fun `get event that exists`() {
        // Given
        val event = createSampleEvent()
        given(eventRepository.findById(event.id)).willReturn(Optional.of(event))

        // When
        val result = eventService.getEvent(event.id)

        // Then
        verify(eventRepository).findById(event.id)
        Assertions.assertEquals(event.title, result.title)
    }

    @Test
    fun `get event that does not exist throws exception`() {
        // Given
        val notFoundEventId = 1L
        given(eventRepository.findById(notFoundEventId)).willReturn(Optional.empty())

        // Then
        val ex =
            Assertions.assertThrows(RecordNotFoundException::class.java) {
                eventService.getEvent(notFoundEventId)
            }

        verify(eventRepository).findById(notFoundEventId)
        Assertions.assertEquals("Event with id $notFoundEventId not found", ex.message)
    }

    @Test
    fun `delete event removes associations and deletes`() {
        // Given
        val event = createSampleEvent()
        given(eventRepository.findById(event.id)).willReturn(Optional.of(event))

        // When
        eventService.deleteEvent(event.id, event.organizer.id)

        // Then
        verify(eventRepository).delete(event)
        Assertions.assertFalse(event.organizer.organizedEvents.contains(event))
        event.participants.forEach {
            Assertions.assertFalse(it.participatedEvents.contains(event))
        }
        event.hobbies.forEach {
            Assertions.assertFalse(it.events.contains(event))
        }
    }

    @Test
    fun `enroll in event adds participant`() {
        val event = createSampleEvent()
        given(eventRepository.findById(event.id)).willReturn(Optional.of(event))
        given(userRepository.findById(participant.id)).willReturn(Optional.of(participant))
        given(eventRepository.save(any())).willAnswer { it.arguments[0] }
        given(userRepository.save(any())).willAnswer { it.arguments[0] }

        // When
        val result = eventService.enrollInEvent(event.id, participant.id)

        // Then
        verify(eventRepository).findById(event.id)
        verify(userRepository).findById(participant.id)
        verify(userRepository).save(participant)
        verify(eventRepository).save(event)
        Assertions.assertTrue(event.participants.contains(participant))
        Assertions.assertTrue(participant.participatedEvents.contains(event))
        Assertions.assertTrue(result.participants.any { it.id == participant.id })
    }

    @Test
    fun `withdraw from event removes participant`() {
        val event =
            createSampleEvent().apply {
                participants.add(participant)
            }
        participant.participatedEvents.add(event)

        given(eventRepository.findById(event.id)).willReturn(Optional.of(event))
        given(userRepository.findById(participant.id)).willReturn(Optional.of(participant))
        given(eventRepository.save(any())).willAnswer { it.arguments[0] }
        given(userRepository.save(any())).willAnswer { it.arguments[0] }

        // When
        val result = eventService.withdrawFromEvent(1L, participant.id)

        // Then
        verify(eventRepository).findById(event.id)
        verify(userRepository).findById(participant.id)
        verify(userRepository).save(participant)
        verify(eventRepository).save(event)
        Assertions.assertFalse(event.participants.contains(participant))
        Assertions.assertFalse(participant.participatedEvents.contains(event))
        Assertions.assertTrue(result.participants.none { it.id == participant.id })
    }

    private fun createSampleEvent(): Event =
        Event(
            id = 1L,
            organizer = eventOrganizer,
            title = "Chess Tournament",
            description = "Big event",
            location = location,
            startTime = now,
            endTime = now.plusHours(2),
            price = 20.0,
            minUsers = 5,
            maxUsers = 20,
            host = venue,
            hobbies = mutableSetOf(hobby),
            participants = mutableSetOf(),
        )
}
