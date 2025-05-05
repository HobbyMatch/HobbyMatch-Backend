package com.github.kkkubakkk.hobbymatchbackend.user.model

import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(100)")
    var name: String,
    @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(320)")
    var email: String,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_hobbies",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "hobby_id", referencedColumnName = "id")],
    )
    var hobbies: MutableSet<Hobby> = mutableSetOf(),
    // TODO: uncomment after reimplementing activity
    @OneToMany(
        mappedBy = "organizer",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    var organizedEvents: MutableSet<Event> = mutableSetOf(),
    @ManyToMany(mappedBy = "participants")
    var participatedEvents: MutableSet<Event> = mutableSetOf(),
    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,
)
