package com.github.kkkubakkk.hobbymatchbackend.bclient.model

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
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
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "bclients")
data class BusinessClient(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    val id: Long = 0,
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(100)")
    var name: String,
    @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(320)")
    var email: String,
    @OneToMany(
        mappedBy = "owner",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    var venues: MutableSet<Venue> = mutableSetOf(),
) {
    fun toDTO(): BusinessClientDTO =
        BusinessClientDTO(
            id = this.id,
            name = this.name,
            email = this.email,
            venues = this.venues.map { it.toDTO() },
        )
}
