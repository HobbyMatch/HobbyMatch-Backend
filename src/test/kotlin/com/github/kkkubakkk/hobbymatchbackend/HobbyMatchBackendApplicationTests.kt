package com.github.kkkubakkk.hobbymatchbackend

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class HobbyMatchBackendApplicationTests {
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
