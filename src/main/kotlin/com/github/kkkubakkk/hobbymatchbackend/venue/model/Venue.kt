package com.github.kkkubakkk.hobbymatchbackend.venue.model

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import com.github.kkkubakkk.hobbymatchbackend.event.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import com.github.kkkubakkk.hobbymatchbackend.location.model.Location
import com.github.kkkubakkk.hobbymatchbackend.venue.dto.VenueDTO
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
    @Embedded
    var location: Location,
    @Column(name = "datetime", nullable = false, columnDefinition = "DATETIME")
    @OneToMany(
        mappedBy = "host",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    var hostedEvents: MutableSet<Event> = mutableSetOf(),
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: BusinessClient,
) {
    fun toDTO(): VenueDTO =
        VenueDTO(
            id = this.id,
            location = this.location,
            hostedActivities = this.hostedEvents.map { it.toDTO() },
            ownerId = this.owner.id,
//            owner = owner.toDTO(),
        )
}
