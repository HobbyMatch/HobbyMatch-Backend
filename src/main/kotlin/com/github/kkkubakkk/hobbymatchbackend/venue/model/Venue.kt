package com.github.kkkubakkk.hobbymatchbackend.venue.model

import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "venues")
data class Venue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
//    @Column(name = "address", updatable = false, nullable = false, columnDefinition = "NVARCHAR(50)")
//    val address: String,
    @Embedded
    var location: Location,
    @Column(name = "datetime", nullable = false, columnDefinition = "DATETIME")
    // One to Many foreign key to the table of activities
    @OneToMany(
        mappedBy = "host",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    var hostedActivities: MutableSet<Activity> = mutableSetOf(),
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: BusinessClient,
)
