package com.github.kkkubakkk.hobbymatchbackend.hobby.model

import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "hobbies")
data class Hobby(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
    @Column(name = "name", nullable = false, unique = true, columnDefinition = "NVARCHAR(100)")
    var name: String,
    @ManyToMany(mappedBy = "hobbies")
    var users: MutableSet<User> = mutableSetOf(),
    @ManyToMany(mappedBy = "hobbies")
    var events: MutableSet<Event> = mutableSetOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hobby) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
