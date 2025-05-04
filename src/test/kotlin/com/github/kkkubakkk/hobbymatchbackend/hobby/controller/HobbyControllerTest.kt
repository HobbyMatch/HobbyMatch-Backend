package com.github.kkkubakkk.hobbymatchbackend.hobby.controller

import com.github.kkkubakkk.hobbymatchbackend.hobby.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.hobby.model.Hobby
import com.github.kkkubakkk.hobbymatchbackend.hobby.service.HobbyService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@WebMvcTest(HobbyController::class)
@AutoConfigureMockMvc(addFilters = false)
class HobbyControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var hobbyService: HobbyService

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
    }

    @Test
    fun `get all hobbies`() {
        val hobby1 =
            Hobby(
                id = 1L,
                name = "Football",
            )
        val hobby2 =
            Hobby(
                id = 2L,
                name = "Volleyball",
            )
        val list = listOf(hobby1.toDTO(), hobby2.toDTO())
        BDDMockito.given(hobbyService.getAllHobbies()).willReturn(list)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/hobbies"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(list)))
    }
}
