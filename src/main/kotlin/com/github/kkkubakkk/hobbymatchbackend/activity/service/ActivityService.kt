package com.github.kkkubakkk.hobbymatchbackend.activity.service

import com.github.kkkubakkk.hobbymatchbackend.activity.dto.ActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.CreateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.EnrollInActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.UpdateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
import com.github.kkkubakkk.hobbymatchbackend.activity.repository.ActivityRepository
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import com.github.kkkubakkk.hobbymatchbackend.venue.repository.VenueRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
    private val venueRepository: VenueRepository,
) {
    fun createActivity(createActivityDTO: CreateActivityDTO): ActivityDTO {
        val organizer = userRepository.findByUsername(createActivityDTO.organizerUsername)
        require(!organizer.isPresent) { "User with this username doesn't exist" }
        val host = venueRepository.findByLocation(createActivityDTO.location)
        require(!host.isPresent) { "Venue in chosen location does not exist" }
        val hobbies = hobbyRepository.findAllByNameIn(createActivityDTO.hobbies.map { it.name })

        val activity =
            Activity(
                organizer = organizer.get(),
                title = createActivityDTO.title,
                description = createActivityDTO.description,
                location = createActivityDTO.location,
                dateTime = LocalDateTime.parse(createActivityDTO.datetime),
                hobbies = hobbies.toMutableSet(),
                host = host.get(),
            )

        organizer.get().organizedActivities.add(activity)
        for (hobby in hobbies) {
            hobby.activities.add(activity)
        }
        // adding activity to the venue
        val venue = host.get()
        venue.hostedActivities.add(activity)
        //
        activityRepository.save(activity)

        return activity.toDTO()
    }

    fun getAllActivities(): List<ActivityDTO> = activityRepository.findAll().map { it.toDTO() }

    @Transactional
    fun updateActivity(
        id: Long,
        updateActivityDTO: UpdateActivityDTO,
        organizerUsername: String,
    ): ActivityDTO {
        val activity = activityRepository.findById(id)
        require(activity.isPresent) { "Activity with id $id not found" }

        val organizer = userRepository.findByUsername(organizerUsername)
        require(organizer.isPresent) { "User with username $organizerUsername not found" }

        // Validate the organizer is the one updating the activity
        require(activity.get().organizer.id == organizer.get().id) {
            "Only the organizer can update this activity"
        }

        val activityEntity = activity.get()
        activityEntity.title = updateActivityDTO.title
        activityEntity.description = updateActivityDTO.description
        activityEntity.location = updateActivityDTO.location
        activityEntity.dateTime = LocalDateTime.parse(updateActivityDTO.datetime)

        // Update hobbies
        val newHobbies = hobbyRepository.findAllByNameIn(updateActivityDTO.hobbies.map { it.name })

        // Remove activity from old hobbies that aren't in the new set
        val hobbiesToRemove = activityEntity.hobbies.filter { !newHobbies.contains(it) }
        for (hobby in hobbiesToRemove) {
            hobby.activities.remove(activityEntity)
        }

        // Add activity to new hobbies
        activityEntity.hobbies.clear()
        activityEntity.hobbies.addAll(newHobbies)

        for (hobby in newHobbies) {
            if (!hobby.activities.contains(activityEntity)) {
                hobby.activities.add(activityEntity)
            }
        }

        return activityRepository.save(activityEntity).toDTO()
    }

    @Transactional
    fun deleteActivity(
        id: Long,
        organizerUsername: String,
    ) {
        val activity = activityRepository.findById(id)
        require(activity.isPresent) { "Activity with id $id not found" }

        val organizer = userRepository.findByUsername(organizerUsername)
        require(organizer.isPresent) { "User with username $organizerUsername not found" }

        // Validate the organizer is the one deleting the activity
        require(activity.get().organizer.id == organizer.get().id) {
            "Only the organizer can delete this activity"
        }

        val activityEntity = activity.get()

        // verify presence of the venue hosting the activity
        val host = venueRepository.findByLocation(activityEntity.location)
        require(host.isPresent) { "Hosting venue not found" }
        val venue = host.get()

        // Remove from participants
        activityEntity.participants.forEach {
            it.participatedActivities.remove(activityEntity)
        }

        // Remove from hobbies
        activityEntity.hobbies.forEach {
            it.activities.remove(activityEntity)
        }

        // Remove from organizer
        activityEntity.organizer.organizedActivities.remove(activityEntity)

        // Remove from venue
        venue.hostedActivities.remove(activityEntity)

        // Delete the activity
        activityRepository.delete(activityEntity)
    }

    fun enrollInActivity(enrollInActivityDTO: EnrollInActivityDTO): ActivityDTO {
        val activity = activityRepository.findById(enrollInActivityDTO.activityId)
        require(!activity.isPresent) { "Activity with this id doesn't exist" }
        val participant = userRepository.findByUsername(enrollInActivityDTO.participantUsername)
        require(!participant.isPresent) { "User with this username doesn't exist" }

        activity.get().participants.add(participant.get())
        participant.get().participatedActivities.add(activity.get())

        activityRepository.save(activity.get())
        userRepository.save(participant.get())

        return activity.get().toDTO()
    }

    fun withdrawFromActivity(enrollInActivityDTO: EnrollInActivityDTO): ActivityDTO {
        val activity = activityRepository.findById(enrollInActivityDTO.activityId)
        require(!activity.isPresent) { "Activity with this id doesn't exist" }
        val participant = userRepository.findByUsername(enrollInActivityDTO.participantUsername)
        require(!participant.isPresent) { "User with this username doesn't exist" }

        activity.get().participants.remove(participant.get())
        participant.get().participatedActivities.remove(activity.get())

        activityRepository.save(activity.get())
        userRepository.save(participant.get())

        return activity.get().toDTO()
    }
}
