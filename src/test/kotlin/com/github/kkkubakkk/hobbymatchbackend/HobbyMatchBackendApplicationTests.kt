package com.github.kkkubakkk.hobbymatchbackend

import com.github.kkkubakkk.hobbymatchbackend.config.TestSecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
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
@Import(TestSecurityConfig::class)
@Testcontainers
class HobbyMatchBackendApplicationTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        @Container
        val sqlServerContainer =
            MSSQLServerContainer("mcr.microsoft.com/mssql/server:2022-latest")
                .acceptLicense()

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", sqlServerContainer::getJdbcUrl)
            registry.add("spring.datasource.username", sqlServerContainer::getUsername)
            registry.add("spring.datasource.password", sqlServerContainer::getPassword)
        }
    }

    @Test
    fun `should return Hello, world! on GET request to hello endpoint`() {
        mockMvc
            .perform(get("/hello"))
            .andExpect(status().isOk)
            .andExpect(content().string("Hello, world!"))
    }
}
