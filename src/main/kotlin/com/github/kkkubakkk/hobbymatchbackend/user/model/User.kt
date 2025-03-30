package com.github.kkkubakkk.hobbymatchbackend.user.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
    @Column(name = "firstName", nullable = false, columnDefinition = "NVARCHAR(50)")
    var firstName: String,
    @Column(name = "lastName", nullable = false, columnDefinition = "NVARCHAR(100)")
    var lastName: String,
    @Column(name = "username", nullable = false, unique = true, columnDefinition = "VARCHAR(30)")
    var username: String,
    @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(320)")
    var email: String,
)
