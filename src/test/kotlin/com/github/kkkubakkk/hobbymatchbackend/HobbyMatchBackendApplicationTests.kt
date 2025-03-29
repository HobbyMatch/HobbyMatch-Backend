package com.github.kkkubakkk.hobbymatchbackend

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.MSSQLServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class HobbyMatchBackendApplicationTests {
    companion object {
        @Container
        val sqlServer =
            MSSQLServerContainer<Nothing>("mcr.microsoft.com/mssql/server:2019-latest")
                .apply {
                    withPassword("A_Str0ng_Password!")
                    withEnv("ACCEPT_EULA", "Y")
                }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { sqlServer.jdbcUrl }
            registry.add("spring.datasource.username") { sqlServer.username }
            registry.add("spring.datasource.password") { sqlServer.password }
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return Hello, world! on GET request to hello endpoint`() {
        mockMvc
            .perform(get("/hello"))
            .andExpect(status().isOk)
            .andExpect(content().string("Hello, world!"))
    }
}
