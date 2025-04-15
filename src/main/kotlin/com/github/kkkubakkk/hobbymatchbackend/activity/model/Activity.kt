package com.github.kkkubakkk.hobbymatchbackend.activity.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "activities")
data class Activity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
    @JsonManagedReference("organizer-activities")
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    val organizer: User,
    @JsonManagedReference("participant-activities")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "activities_participants",
        joinColumns = [JoinColumn(name = "activity_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")],
    )
    var participants: MutableSet<User> = mutableSetOf(),
    @Column(name = "title", nullable = false, columnDefinition = "NVARCHAR(100)")
    var title: String,
    @Column(name = "description", nullable = true, columnDefinition = "NVARCHAR(MAX)")
    var description: String? = null,
    @Embedded
    var location: Location,
    @Column(name = "datetime", nullable = false, columnDefinition = "DATETIME")
    var dateTime: LocalDateTime,
    @JsonManagedReference("activity-hobbies")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "activities_hobbies",
        joinColumns = [JoinColumn(name = "activity_id")],
        inverseJoinColumns = [JoinColumn(name = "hobby_id")],
    )
    var hobbies: MutableSet<Hobby> = mutableSetOf(),
)
