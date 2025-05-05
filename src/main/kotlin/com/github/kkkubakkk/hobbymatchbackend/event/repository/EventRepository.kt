package com.github.kkkubakkk.hobbymatchbackend.event.repository

import com.github.kkkubakkk.hobbymatchbackend.event.model.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : JpaRepository<Event, Long>
