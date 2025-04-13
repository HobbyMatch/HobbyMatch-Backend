package com.github.kkkubakkk.hobbymatchbackend.user.controller

import com.github.kkkubakkk.hobbymatchbackend.user.dto.CreateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.EmailUpdateDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.SearchUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO
import com.github.kkkubakkk.hobbymatchbackend.user.model.User
import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.doNothing
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import kotlin.IllegalArgumentException

@WebMvcTest(UserController::class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userService: UserService

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
    }

    @Test
    fun `get list of all users`() {
        val user1 =
            User(
                id = 1L,
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                email = "john.doe@example.com",
            )
        val user2 =
            User(
                id = 2L,
                firstName = "Lin",
                lastName = "Doe",
                username = "linndoe",
                email = "lin.doe@example.com",
            )
        val users = listOf(user1.toDTO(), user2.toDTO())
        given(userService.getAllUsers()).willReturn(users)
        mockMvc
            .perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(users)))
    }

    @Test
    fun `get empty list for an empty database`() {
        given(userService.getAllUsers()).willReturn(listOf())
        mockMvc
            .perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(listOf<UserDTO>())))
    }

    @Test
    fun `post a new user`() {
        val firstName = "John"
        val lastName = "Doe"
        val username = "johndoe"
        val email = "john.doe@example.com"
        val createUserDTO =
            CreateUserDTO(
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
            )
        val user =
            User(
                id = 1L,
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
            )
        val userDTO = user.toDTO()
        given(userService.createUser(createUserDTO)).willReturn(userDTO)
        mockMvc
            .perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createUserDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun `404 status for posting a user with already existing email`() {
        val createUserDTO =
            CreateUserDTO(
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                email = "john.doe@example.com",
            )
        given(userService.createUser(createUserDTO))
            .willThrow(IllegalArgumentException("User with this email already exists"))

        mockMvc
            .perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createUserDTO)),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `get user by id`() {
        val userId = 1L
        val user =
            User(
                id = userId,
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                email = "john.doe@example.com",
            )
        val userDTO = user.toDTO()
        given(userService.getUserById(userId)).willReturn(userDTO)
        mockMvc
            .perform(get("/api/users/$userId"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun `404 status when user not found by id`() {
        val id = 1L
        given(userService.getUserById(id))
            .willThrow(IllegalArgumentException("User not found"))

        mockMvc
            .perform(get("/api/users/$id"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get user by email`() {
        val userEmail = "john.doe@example.com"
        val user =
            User(
                id = 1,
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                email = userEmail,
            )
        val userDTO = user.toDTO()
        given(userService.getUserByEmail(userEmail)).willReturn(userDTO)
        mockMvc
            .perform(get("/api/users/email/$userEmail"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun `404 status when user not found by email`() {
        val email = "john.doe@example.com"
        given(userService.getUserByEmail(email))
            .willThrow(IllegalArgumentException("User not found"))
        mockMvc
            .perform(get("/api/users/email/$email"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get user by username`() {
        val username = "johndoe"
        val user =
            User(
                id = 1,
                firstName = "John",
                lastName = "Doe",
                username = username,
                email = "john.doe@example.com",
            )
        val userDTO = user.toDTO()
        given(userService.getUserByUsername(username)).willReturn(userDTO)
        mockMvc
            .perform(get("/api/users/username/$username"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun `404 status when user not found by username`() {
        val username = "johndoe"
        given(userService.getUserByUsername(username))
            .willThrow(IllegalArgumentException("User not found"))
        mockMvc
            .perform(get("/api/users/username/$username"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get user existence by email for an existing user`() {
        val email = "john.doe@example.com"
        given(userService.userExists(email)).willReturn(true)
        mockMvc
            .perform(get("/api/users/exists/$email"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(mapOf("exists" to true))))
    }

    @Test
    fun `get user existence by email for a non-existing user`() {
        val email = "john.doe@example.com"
        given(userService.userExists(email)).willReturn(false)
        mockMvc
            .perform(get("/api/users/exists/$email"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(mapOf("exists" to false))))
    }

    @Test
    fun `update existing user by id`() {
        val id = 1L
        val updateUserDTO =
            UpdateUserDTO(
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
            )
        val userDTO =
            UserDTO(
                id = id,
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                email = "john.doe@example.com",
            )
        given(userService.updateUser(id, updateUserDTO)).willReturn(userDTO)
        mockMvc
            .perform(
                put("/api/users/$id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateUserDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun `404 status for updating a non-existing user by id`() {
        val id = 1L
        val updateUserDTO =
            UpdateUserDTO(
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
            )
        given(userService.updateUser(id, updateUserDTO))
            .willThrow(IllegalArgumentException("User not found"))
        mockMvc
            .perform(
                put("/api/users/$id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateUserDTO)),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `update an existing user by email`() {
        val email = "john.doe@example.com"
        val userDTO =
            UserDTO(
                id = 1L,
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                email = email,
            )
        given(userService.updateUserByEmail(email, userDTO)).willReturn(userDTO)
        mockMvc
            .perform(
                put("/api/users/email/$email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun `404 status for updating a non-existing user by email`() {
        val email = "john.doe@example.com"
        val userDTO =
            UserDTO(
                id = 1L,
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                email = email,
            )
        given(userService.updateUserByEmail(email, userDTO))
            .willThrow(IllegalArgumentException("User not found"))
        mockMvc
            .perform(
                put("/api/users/email/$email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDTO)),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `update an existing user email by id`() {
        val id = 1L
        val email = "john.doe@example.com"
        val emailUpdateDTO =
            EmailUpdateDTO(
                email = email,
            )
        val userDTO =
            UserDTO(
                id = id,
                firstName = "John",
                lastName = "Doe",
                username = "johndoe",
                email = email,
            )
        given(userService.updateUserEmail(id, emailUpdateDTO)).willReturn(userDTO)
        mockMvc
            .perform(
                put("/api/users/$id/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(emailUpdateDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun `404 status when updating a non-existing user email by id`() {
        val id = 1L
        val email = "john.doe@example.com"
        val emailUpdateDTO =
            EmailUpdateDTO(
                email = email,
            )
        given(userService.updateUserEmail(id, emailUpdateDTO))
            .willThrow(IllegalArgumentException("User not found"))
        mockMvc
            .perform(
                put("/api/users/$id/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(emailUpdateDTO)),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `404 status when updating a user email with already existing email by id`() {
        val id = 1L
        val email = "john.doe@example.com"
        val emailUpdateDTO =
            EmailUpdateDTO(
                email = email,
            )
        given(userService.updateUserEmail(id, emailUpdateDTO))
            .willThrow(IllegalArgumentException("Email already in use"))
        mockMvc
            .perform(
                put("/api/users/$id/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(emailUpdateDTO)),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun `delete user by id`() {
        val id = 1L
        doNothing().`when`(userService).deleteUser(id)
        mockMvc
            .perform(delete("/api/users/$id"))
            .andExpect(status().isNoContent)
        verify(userService).deleteUser(id)
    }

    @Test
    fun `search user by first name, last name, username and email`() {
        val firstName = "John"
        val lastName = "Doe"
        val username = "johndoe"
        val email = "john.doe@example.com"
        val searchUserDTO =
            SearchUserDTO(
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
            )
        val users =
            listOf(
                UserDTO(
                    id = 1L,
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    email = email,
                ),
            )
        given(userService.searchUsers(searchUserDTO))
            .willReturn(users)
        mockMvc
            .perform(
                post("/api/users/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(searchUserDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(users)))
    }

    @Test
    fun `search users by last name, username and email`() {
        val lastName = "Doe"
        val username = "johndoe"
        val email = "john.doe@example.com"
        val searchUserDTO =
            SearchUserDTO(
                firstName = null,
                lastName = lastName,
                username = username,
                email = email,
            )
        val users =
            listOf(
                UserDTO(
                    id = 1L,
                    firstName = "John",
                    lastName = lastName,
                    username = username,
                    email = email,
                ),
                UserDTO(
                    id = 1L,
                    firstName = "Jane",
                    lastName = lastName,
                    username = username,
                    email = email,
                ),
            )
        given(userService.searchUsers(searchUserDTO))
            .willReturn(users)
        mockMvc
            .perform(
                post("/api/users/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(searchUserDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(users)))
    }

    @Test
    fun `search users by username and email`() {
        val username = "johndoe"
        val email = "john.doe@example.com"
        val searchUserDTO =
            SearchUserDTO(
                firstName = null,
                lastName = null,
                username = username,
                email = email,
            )
        val users =
            listOf(
                UserDTO(
                    id = 1L,
                    firstName = "John",
                    lastName = "Travolta",
                    username = username,
                    email = email,
                ),
                UserDTO(
                    id = 1L,
                    firstName = "Jane",
                    lastName = "Doe",
                    username = username,
                    email = email,
                ),
            )
        given(userService.searchUsers(searchUserDTO))
            .willReturn(users)
        mockMvc
            .perform(
                post("/api/users/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(searchUserDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(users)))
    }

    @Test
    fun `search users by email`() {
        val email = "john.doe@example.com"
        val searchUserDTO =
            SearchUserDTO(
                firstName = null,
                lastName = null,
                username = null,
                email = email,
            )
        val users =
            listOf(
                UserDTO(
                    id = 1L,
                    firstName = "John",
                    lastName = "Travolta",
                    username = "johntravolta",
                    email = email,
                ),
                UserDTO(
                    id = 1L,
                    firstName = "Jane",
                    lastName = "Doe",
                    username = "janedoe",
                    email = email,
                ),
            )
        given(userService.searchUsers(searchUserDTO))
            .willReturn(users)
        mockMvc
            .perform(
                post("/api/users/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(searchUserDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(users)))
    }

    @Test
    fun `search users without filters`() {
        val searchUserDTO =
            SearchUserDTO(
                firstName = null,
                lastName = null,
                username = null,
                email = null,
            )
        val users =
            listOf(
                UserDTO(
                    id = 1L,
                    firstName = "John",
                    lastName = "Travolta",
                    username = "johntravolta",
                    email = "john.travolta@example.com",
                ),
                UserDTO(
                    id = 1L,
                    firstName = "Jane",
                    lastName = "Doe",
                    username = "janedoe",
                    email = "jane.doe@example.com",
                ),
            )
        given(userService.searchUsers(searchUserDTO))
            .willReturn(users)
        mockMvc
            .perform(
                post("/api/users/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(searchUserDTO)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(users)))
    }
}
