package com.github.kkkubakkk.hobbymatchbackend.user.repository

import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>
