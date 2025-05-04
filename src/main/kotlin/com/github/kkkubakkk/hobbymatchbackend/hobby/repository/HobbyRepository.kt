package com.github.kkkubakkk.hobbymatchbackend.hobby.repository

import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HobbyRepository : JpaRepository<Hobby, Long> {
    fun findAllByNameIn(names: List<String>): List<Hobby>

    fun findByName(name: String): Optional<Hobby>
}
