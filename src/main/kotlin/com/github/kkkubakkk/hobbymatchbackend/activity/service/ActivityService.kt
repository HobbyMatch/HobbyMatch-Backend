package com.github.kkkubakkk.hobbymatchbackend.activity.service

import com.github.kkkubakkk.hobbymatchbackend.activity.dto.ActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.CreateActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.EnrollInActivityDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
import com.github.kkkubakkk.hobbymatchbackend.activity.repository.ActivityRepository
import com.github.kkkubakkk.hobbymatchbackend.hobby.repository.HobbyRepository
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val hobbyRepository: HobbyRepository,
) {
    fun createActivity(createActivityDTO: CreateActivityDTO): ActivityDTO {
        val organizer = userRepository.findByUsername(createActivityDTO.organizerUsername)
        require(!organizer.isPresent) { "User with this username doesn't exist" }
        val hobbies = hobbyRepository.findAllByNameIn(createActivityDTO.hobbies.map { it.name })

        val activity =
            Activity(
                organizer = organizer.get(),
                title = createActivityDTO.title,
                description = createActivityDTO.description,
                location = Location(longitude = createActivityDTO.longitude, latitude = createActivityDTO.latitude),
                dateTime = LocalDateTime.parse(createActivityDTO.datetime),
                hobbies = hobbies.toMutableSet(),
            )

        organizer.get().organizedActivities.add(activity)
        for (hobby in hobbies) {
            hobby.activities.add(activity)
        }

        activityRepository.save(activity)

        return activity.toDTO()
    }

    fun getAllActivities(): List<ActivityDTO> = activityRepository.findAll().map { it.toDTO() }

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
