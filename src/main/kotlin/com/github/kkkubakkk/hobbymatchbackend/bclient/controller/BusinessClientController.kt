package com.github.kkkubakkk.hobbymatchbackend.bclient.controller

import com.github.kkkubakkk.hobbymatchbackend.bclient.service.BusinessClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RequestMapping("/api/v1")
@RestController
class BusinessClientController(
    private val clientService: BusinessClientService
) {
    @GetMapping("/business/{clientId}")
    fun getClientById(@PathVariable clientId: Long): ResponseEntity<BusinessClientDTO> =
        try {
            ResponseEntity.ok(clientService.getClientById(clientId))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
}
