package com.github.kkkubakkk.hobbymatchbackend.hobby.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
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
    @JsonBackReference("activity-hobbies")
    @ManyToMany(mappedBy = "hobbies")
    var activities: MutableSet<Activity> = mutableSetOf(),
)
