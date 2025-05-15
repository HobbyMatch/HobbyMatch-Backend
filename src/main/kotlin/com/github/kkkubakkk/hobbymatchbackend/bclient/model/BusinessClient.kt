package com.github.kkkubakkk.hobbymatchbackend.bclient.model

import com.github.kkkubakkk.hobbymatchbackend.venue.model.Venue
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "bclients")
data class BusinessClient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
    @Column(name = "name", updatable = true, nullable = false, columnDefinition = "NVARCHAR(100)")
    var name: String,
    @Column(name = "email", updatable = true, nullable = false, unique = true, columnDefinition = "VARCHAR(320)")
    var email: String,
    @Column(name = "taxId", updatable = true, nullable = true, unique = true)
    var taxId: String,
    @OneToMany(
        mappedBy = "owner",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    var venues: MutableSet<Venue> = mutableSetOf(),
)
