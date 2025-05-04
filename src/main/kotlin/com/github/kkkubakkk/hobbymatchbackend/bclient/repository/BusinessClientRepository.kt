package com.github.kkkubakkk.hobbymatchbackend.bclient.repository

import com.github.kkkubakkk.hobbymatchbackend.bclient.model.BusinessClient
import org.springframework.data.jpa.repository.JpaRepository

interface BusinessClientRepository: JpaRepository<BusinessClient, Long> {
}