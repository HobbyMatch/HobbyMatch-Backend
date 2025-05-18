package com.github.kkkubakkk.hobbymatchbackend.bclient.controller

import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.BusinessClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.UpdateClientDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils.Companion.getAuthenticatedUserId
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/businessclients")
class BusinessClientController(
    private val clientService: BusinessClientService,
) {
    private val logger = LoggerFactory.getLogger(BusinessClientController::class.java)

    @GetMapping("/{clientId}")
    fun getBusinessClient(
        @PathVariable clientId: Long,
    ): ResponseEntity<BusinessClientDTO> = ResponseEntity.ok(clientService.getClient(clientId).toDTO())

    @GetMapping("/me")
    fun getMe(): ResponseEntity<BusinessClientDTO> {
        val id = getAuthenticatedUserId()
        return ResponseEntity.ok(clientService.getClient(id).toDTO())
//        try {
//            logger.info("Fetching business client information")
//            val id = getAuthenticatedUserId()
//            return ResponseEntity.ok(clientService.getMe(id).toDTO())
//        } catch (e: Exception) {
//            logger.error("Error fetching business client information", e)
//            return ResponseEntity.notFound().build()
//        }
    }

    @PutMapping("/{clientId}")
    fun updateBusinessClient(
        @PathVariable clientId: Long,
        @RequestBody updateClientDTO: UpdateClientDTO,
    ): ResponseEntity<BusinessClientDTO> = ResponseEntity.ok(clientService.updateClientById(clientId, updateClientDTO))
}
