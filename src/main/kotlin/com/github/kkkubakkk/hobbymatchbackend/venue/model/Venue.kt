package com.github.kkkubakkk.hobbymatchbackend.venue.model

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "venues")
data class Venue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
    @Embedded
    var location: Location,
    @Column(name = "datetime", nullable = false, columnDefinition = "DATETIME")
//    @OneToMany(
//        mappedBy = "host",
//        cascade = [CascadeType.ALL],
//        orphanRemoval = true,
//        fetch = FetchType.LAZY,
//    )
//    var hostedActivities: MutableSet<Activity> = mutableSetOf(),
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: BusinessClient,
)
