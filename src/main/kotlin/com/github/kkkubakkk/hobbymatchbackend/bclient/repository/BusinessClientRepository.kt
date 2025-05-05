package com.github.kkkubakkk.hobbymatchbackend.bclient.repository

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BusinessClientRepository : JpaRepository<BusinessClient, Long> {
    fun findByEmail(email: String): Optional<BusinessClient>
}
