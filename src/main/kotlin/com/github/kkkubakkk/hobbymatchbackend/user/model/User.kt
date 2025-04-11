package com.github.kkkubakkk.hobbymatchbackend.user.model

import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDate

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
    @ManyToMany
    @JoinTable(
        name = "users_hobbies",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "hobby_id", referencedColumnName = "id")],
    )
    var hobbies: MutableSet<Hobby> = mutableSetOf(),
    @Column(name = "birthday", nullable = false, updatable = false, columnDefinition = "DATE")
    val birthday: LocalDate,
    @Column(name = "bio", nullable = true, columnDefinition = "NVARCHAR(MAX)")
    var bio: String? = null,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as User

        return id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String =
        this::class.simpleName +
            "(  id = $id   ,   " +
            "firstName = $firstName   ,   " +
            "lastName = $lastName   ,   " +
            "username = $username   ,   " +
            "email = $email )"
}
