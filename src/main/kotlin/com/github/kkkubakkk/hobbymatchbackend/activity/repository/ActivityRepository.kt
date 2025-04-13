package com.github.kkkubakkk.hobbymatchbackend.activity.repository

import com.github.kkkubakkk.hobbymatchbackend.activity.model.Activity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ActivityRepository : JpaRepository<Activity, Long>
