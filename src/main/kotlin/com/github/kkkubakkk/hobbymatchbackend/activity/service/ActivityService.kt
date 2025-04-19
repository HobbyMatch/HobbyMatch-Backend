package com.github.kkkubakkk.hobbymatchbackend.activity.service

import com.github.kkkubakkk.hobbymatchbackend.activity.dto.ActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.CreateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.EnrollInActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.UpdateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.WithdrawFromActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
import com.github.kkkubakkk.hobbymatchbackend.activity.repository.ActivityRepository
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
) {
    fun createActivity(createActivityDTO: CreateActivityDTO): ActivityDTO {
        val organizer = userRepository.findByUsername(createActivityDTO.organizerEmail)
        require(organizer.isPresent) { "User with this username doesn't exist" }
        val hobby = hobbyRepository.findByName(createActivityDTO.hobby.name)
        require(hobby != null) { "Hobby with this name doesn't exist" }

        val activity =
            Activity(
                organizer = organizer.get(),
                maxParticipants = createActivityDTO.maxParticipants,
                title = createActivityDTO.title,
                description = createActivityDTO.description,
                location = createActivityDTO.location,
                dateTime = LocalDateTime.parse(createActivityDTO.datetime),
                hobby = hobby,
            )

        organizer.get().organizedActivities.add(activity)
        hobby.activities.add(activity)

        activityRepository.save(activity)

        return activity.toDTO()
    }

    @Transactional(readOnly = true)
    fun getAllActivities(): List<ActivityDTO> = activityRepository.findAll().map { it.toDTO() }

    @Transactional
    fun updateActivity(
        id: Long,
        updateActivityDTO: UpdateActivityDTO,
        organizerEmail: String,
    ): ActivityDTO {
        val activity = activityRepository.findById(id)
        require(activity.isPresent) { "Activity with id $id not found" }

        val organizer = userRepository.findByEmail(organizerEmail)
        require(organizer.isPresent) { "User with email $organizerEmail not found" }

        // TODO: Do it better (really validate the user is the organizer, maybe from the token)
        require(activity.get().organizer.id == organizer.get().id) {
            "Only the organizer can update this activity"
        }

        val activityEntity = activity.get()
        activityEntity.maxParticipants = updateActivityDTO.maxParticipants
        activityEntity.title = updateActivityDTO.title
        activityEntity.description = updateActivityDTO.description
        activityEntity.location = updateActivityDTO.location
        activityEntity.dateTime = LocalDateTime.parse(updateActivityDTO.datetime)

        // Update hobby
        val newHobby = hobbyRepository.findByName(updateActivityDTO.hobby.name)
        require(newHobby != null) { "Hobby with this name doesn't exist" }

        // Set new hobby
        activityEntity.hobby.activities.remove(activityEntity)
        activityEntity.hobby = newHobby

        // Add activity to new hobby
        activityEntity.hobby.activities.add(activityEntity)

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

        // Remove from participants
        activityEntity.participants.forEach {
            it.participatedActivities.remove(activityEntity)
        }

        // Remove from hobbies
        activityEntity.hobby.activities.remove(activityEntity)

        // Remove from organizer
        activityEntity.organizer.organizedActivities.remove(activityEntity)

        // Delete the activity
        activityRepository.delete(activityEntity)
    }

    fun enrollInActivity(enrollInActivityDTO: EnrollInActivityDTO): ActivityDTO {
        val activity = activityRepository.findById(enrollInActivityDTO.activityId)
        require(activity.isPresent) { "Activity with this id doesn't exist" }
        val participant = userRepository.findByUsername(enrollInActivityDTO.participantEmail)
        require(participant.isPresent) { "User with this username doesn't exist" }

        activity.get().participants.add(participant.get())
        participant.get().participatedActivities.add(activity.get())

        activityRepository.save(activity.get())
        userRepository.save(participant.get())

        return activity.get().toDTO()
    }

    fun withdrawFromActivity(withdrawFromActivity: WithdrawFromActivityDTO): ActivityDTO {
        val activity = activityRepository.findById(withdrawFromActivity.activityId)
        require(activity.isPresent) { "Activity with this id doesn't exist" }
        val participant = userRepository.findByUsername(withdrawFromActivity.participantEmail)
        require(participant.isPresent) { "User with this username doesn't exist" }

        activity.get().participants.remove(participant.get())
        participant.get().participatedActivities.remove(activity.get())

        activityRepository.save(activity.get())
        userRepository.save(participant.get())

        return activity.get().toDTO()
    }
}
