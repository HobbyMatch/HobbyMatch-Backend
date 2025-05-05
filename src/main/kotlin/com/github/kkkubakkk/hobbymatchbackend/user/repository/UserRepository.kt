package com.github.kkkubakkk.hobbymatchbackend.user.repository

import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
}
