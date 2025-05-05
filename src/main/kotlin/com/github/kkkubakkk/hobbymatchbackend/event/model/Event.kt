package com.github.kkkubakkk.hobbymatchbackend.event.model

import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
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
@Table(name = "events")
data class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    val organizer: User,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "events_participants",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")],
    )
    var participants: MutableSet<User> = mutableSetOf(),
    // Adding the Many to One foreign key to the table venues
    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    val host: Venue,
    //
    @Column(name = "title", nullable = false, columnDefinition = "NVARCHAR(100)")
    var title: String,
    @Column(name = "description", nullable = true, columnDefinition = "NVARCHAR(MAX)")
    var description: String? = null,
    @Embedded
    var location: Location,
    @Column(name = "startTime", nullable = false, columnDefinition = "DATETIME")
    var startTime: LocalDateTime,
    @Column(name = "endTime", nullable = false, columnDefinition = "DATETIME")
    var endTime: LocalDateTime,
    @Column(name = "price", nullable = false, columnDefinition = "MONEY")
    var price: Double,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "events_hobbies",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "hobby_id")],
    )
    var hobbies: MutableSet<Hobby> = mutableSetOf(),
    @Column(name = "minUsers", nullable = false, columnDefinition = "INT")
    var minUsers: Int,
    @Column(name = "maxUsers", nullable = false, columnDefinition = "INT")
    var maxUsers: Int,
)
