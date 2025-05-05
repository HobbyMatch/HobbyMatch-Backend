package com.github.kkkubakkk.hobbymatchbackend.bclient.controller

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils.Companion.getAuthenticatedUserId
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/business")
class BusinessClientController(
    private val businessClientService: BusinessClientService,
) {
    private val logger = LoggerFactory.getLogger(BusinessClientController::class.java)

    @GetMapping("/me")
    fun getMe(): ResponseEntity<BusinessClientDTO> {
        try {
            logger.info("Fetching business client information")
            val id = getAuthenticatedUserId()
            return ResponseEntity.ok(businessClientService.getMe(id).toDTO())
        } catch (e: Exception) {
            logger.error("Error fetching business client information", e)
            return ResponseEntity.notFound().build()
        }
    }
}
