// package com.github.kkkubakkk.hobbymatchbackend.user.controller
//
// import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtAuthenticationFilter
// import com.github.kkkubakkk.hobbymatchbackend.security.component.JwtUtils
// import com.github.kkkubakkk.hobbymatchbackend.user.dto.CreateUserDTO
// import com.github.kkkubakkk.hobbymatchbackend.user.dto.UpdateUserDTO
// import com.github.kkkubakkk.hobbymatchbackend.user.dto.UserDTO
// import com.github.kkkubakkk.hobbymatchbackend.user.dto.toDTO
// import com.github.kkkubakkk.hobbymatchbackend.user.model.User
// import com.github.kkkubakkk.hobbymatchbackend.user.service.UserService
// import org.junit.jupiter.api.BeforeEach
// import org.junit.jupiter.api.Test
// import org.mockito.BDDMockito.given
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
// import org.springframework.http.MediaType
// import org.springframework.test.context.bean.override.mockito.MockitoBean
// import org.springframework.test.web.servlet.MockMvc
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
// import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
// import java.time.LocalDate
// import kotlin.IllegalArgumentException
//
// // TODO: Update
// @WebMvcTest(UserController::class)
// @AutoConfigureMockMvc(addFilters = false)
// class UserControllerTest {
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @MockitoBean
//    private lateinit var userService: UserService
//
//    @MockitoBean
//    private lateinit var jwtUtils: JwtUtils
//
//    @MockitoBean
//    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
//
//    private lateinit var objectMapper: ObjectMapper
//
//    @BeforeEach
//    fun setup() {
//        objectMapper = ObjectMapper()
//    }
//
//    @Test
//    fun `post a new user`() {
//        val firstName = "John"
//        val lastName = "Doe"
//        val username = "johndoe"
//        val email = "john.doe@example.com"
//        val user =
//            User(
//                id = 1L,
//                name = "$firstName $lastName",
//                email = email,
//            )
//        val userDTO = user.toDTO()
//        given(userService.createUser(email, "$firstName $lastName")).willReturn(user)
//        mockMvc
//            .perform(
//                post("/api/users")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(createUserDTO)),
//            ).andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
//    }
//
//    @Test
//    fun `404 status for posting a user with already existing email`() {
//        val createUserDTO =
//            CreateUserDTO(
//                firstName = "John",
//                lastName = "Doe",
//                username = "johndoe",
//                email = "john.doe@example.com",
//                birthday = "01.01.1973",
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        given(userService.createUser(createUserDTO))
//            .willThrow(IllegalArgumentException("User with this email already exists"))
//
//        mockMvc
//            .perform(
//                post("/api/users")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(createUserDTO)),
//            ).andExpect(status().isNotFound)
//    }
//
//    @Test
//    fun `get user by email`() {
//        val userEmail = "john.doe@example.com"
//        val user =
//            User(
//                id = 1,
//                firstName = "John",
//                lastName = "Doe",
//                username = "johndoe",
//                email = userEmail,
//                birthday = LocalDate.of(1980, 1, 1),
//                bio = "Bio",
//                isActive = true,
//            )
//        val userDTO = user.toDTO()
//        given(userService.getUserByEmail(userEmail)).willReturn(userDTO)
//        mockMvc
//            .perform(get("/api/users/email/$userEmail"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
//    }
//
//    @Test
//    fun `404 status when user not found by email`() {
//        val email = "john.doe@example.com"
//        given(userService.getUserByEmail(email))
//            .willThrow(IllegalArgumentException("User not found"))
//        mockMvc
//            .perform(get("/api/users/email/$email"))
//            .andExpect(status().isNotFound)
//    }
//
//    @Test
//    fun `get user by username`() {
//        val username = "johndoe"
//        val user =
//            User(
//                id = 1,
//                firstName = "John",
//                lastName = "Doe",
//                username = username,
//                email = "john.doe@example.com",
//                birthday = LocalDate.of(1980, 1, 1),
//                bio = "Bio",
//                isActive = true,
//            )
//        val userDTO = user.toDTO()
//        given(userService.getUserByUsername(username)).willReturn(userDTO)
//        mockMvc
//            .perform(get("/api/users/username/$username"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
//    }
//
//    @Test
//    fun `404 status when user not found by username`() {
//        val username = "johndoe"
//        given(userService.getUserByUsername(username))
//            .willThrow(IllegalArgumentException("User not found"))
//        mockMvc
//            .perform(get("/api/users/username/$username"))
//            .andExpect(status().isNotFound)
//    }
//
//    @Test
//    fun `get user existence by email for an existing user`() {
//        val email = "john.doe@example.com"
//        given(userService.userExists(email)).willReturn(true)
//        mockMvc
//            .perform(get("/api/users/exists/$email"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(mapOf("exists" to true))))
//    }
//
//    @Test
//    fun `get user existence by email for a non-existing user`() {
//        val email = "john.doe@example.com"
//        given(userService.userExists(email)).willReturn(false)
//        mockMvc
//            .perform(get("/api/users/exists/$email"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(mapOf("exists" to false))))
//    }
//
//    @Test
//    fun `update an existing user by email`() {
//        val email = "john.doe@example.com"
//        val updateUserDTO =
//            UpdateUserDTO(
//                firstName = "John",
//                lastName = "Doe",
//                username = "johndoe",
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        val userDTO =
//            UserDTO(
//                id = 1L,
//                firstName = "John",
//                lastName = "Doe",
//                username = "johndoe",
//                email = email,
//                birthday = "01.01.1973",
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        given(userService.updateUserByEmail(email, updateUserDTO)).willReturn(userDTO)
//        mockMvc
//            .perform(
//                put("/api/users/email/$email")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(updateUserDTO)),
//            ).andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
//    }
//
//    @Test
//    fun `404 status for updating a non-existing user by email`() {
//        val email = "john.doe@example.com"
//        val updateUserDTO =
//            UpdateUserDTO(
//                firstName = "John",
//                lastName = "Doe",
//                username = "johndoe",
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        given(userService.updateUserByEmail(email, updateUserDTO))
//            .willThrow(IllegalArgumentException("User not found"))
//        mockMvc
//            .perform(
//                put("/api/users/email/$email")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(updateUserDTO)),
//            ).andExpect(status().isNotFound)
//    }
//
//    @Test
//    fun `update an existing user by username`() {
//        val username = "johndoe"
//        val updateUserDTO =
//            UpdateUserDTO(
//                firstName = "John",
//                lastName = "Doe",
//                username = username,
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        val userDTO =
//            UserDTO(
//                id = 1L,
//                firstName = "John",
//                lastName = "Doe",
//                username = username,
//                email = "john.doe@example.com",
//                birthday = "01.01.1973",
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        given(userService.updateUserByUsername(username, updateUserDTO)).willReturn(userDTO)
//        mockMvc
//            .perform(
//                put("/api/users/username/$username")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(updateUserDTO)),
//            ).andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
//    }
//
//    @Test
//    fun `404 status for updating a non-existing user by username`() {
//        val username = "johndoe"
//        val updateUserDTO =
//            UpdateUserDTO(
//                firstName = "John",
//                lastName = "Doe",
//                username = username,
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        given(userService.updateUserByUsername(username, updateUserDTO))
//            .willThrow(IllegalArgumentException("User not found"))
//        mockMvc
//            .perform(
//                put("/api/users/username/$username")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(updateUserDTO)),
//            ).andExpect(status().isNotFound)
//    }
//
//    @Test
//    fun `search user by first name, last name, username and email`() {
//        val firstName = "John"
//        val lastName = "Doe"
//        val username = "johndoe"
//        val email = "john.doe@example.com"
//        val searchUserDTO =
//            SearchUserDTO(
//                firstName = firstName,
//                lastName = lastName,
//                username = username,
//                email = email,
//            )
//        val users =
//            listOf(
//                UserDTO(
//                    id = 1L,
//                    firstName = firstName,
//                    lastName = lastName,
//                    username = username,
//                    email = email,
//                    birthday = "01.01.1973",
//                    bio = "Bio",
//                    hobbies = listOf(),
//                ),
//            )
//        given(userService.searchUsers(searchUserDTO))
//            .willReturn(users)
//        mockMvc
//            .perform(
//                post("/api/users/search")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(searchUserDTO)),
//            ).andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(users)))
//    }
//
//    @Test
//    fun `search users by last name, username and email`() {
//        val lastName = "Doe"
//        val username = "johndoe"
//        val email = "john.doe@example.com"
//        val searchUserDTO =
//            SearchUserDTO(
//                firstName = null,
//                lastName = lastName,
//                username = username,
//                email = email,
//            )
//        val users =
//            listOf(
//                UserDTO(
//                    id = 1L,
//                    firstName = "John",
//                    lastName = lastName,
//                    username = username,
//                    email = email,
//                    birthday = "01.01.1973",
//                    bio = "Bio",
//                    hobbies = listOf(),
//                ),
//            )
//        given(userService.searchUsers(searchUserDTO))
//            .willReturn(users)
//        mockMvc
//            .perform(
//                post("/api/users/search")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(searchUserDTO)),
//            ).andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(users)))
//    }
//
//    @Test
//    fun `search users by username and email`() {
//        val username = "johndoe"
//        val email = "john.doe@example.com"
//        val searchUserDTO =
//            SearchUserDTO(
//                firstName = null,
//                lastName = null,
//                username = username,
//                email = email,
//            )
//        val users =
//            listOf(
//                UserDTO(
//                    id = 1L,
//                    firstName = "John",
//                    lastName = "Travolta",
//                    username = username,
//                    email = email,
//                    birthday = "01.01.1973",
//                    bio = "Bio",
//                    hobbies = listOf(),
//                ),
//            )
//        given(userService.searchUsers(searchUserDTO))
//            .willReturn(users)
//        mockMvc
//            .perform(
//                post("/api/users/search")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(searchUserDTO)),
//            ).andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(users)))
//    }
//
//    @Test
//    fun `search users by email`() {
//        val email = "john.doe@example.com"
//        val searchUserDTO =
//            SearchUserDTO(
//                firstName = null,
//                lastName = null,
//                username = null,
//                email = email,
//            )
//        val users =
//            listOf(
//                UserDTO(
//                    id = 1L,
//                    firstName = "John",
//                    lastName = "Travolta",
//                    username = "johntravolta",
//                    email = email,
//                    birthday = "01.01.1973",
//                    bio = "Bio",
//                    hobbies = listOf(),
//                ),
//            )
//        given(userService.searchUsers(searchUserDTO))
//            .willReturn(users)
//        mockMvc
//            .perform(
//                post("/api/users/search")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(searchUserDTO)),
//            ).andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(users)))
//    }
//
//    @Test
//    fun `search users without filters`() {
//        val searchUserDTO =
//            SearchUserDTO(
//                firstName = null,
//                lastName = null,
//                username = null,
//                email = null,
//            )
//        val users =
//            listOf(
//                UserDTO(
//                    id = 1L,
//                    firstName = "John",
//                    lastName = "Travolta",
//                    username = "johntravolta",
//                    email = "john.travolta@example.com",
//                    birthday = "01.01.1973",
//                    bio = "Bio",
//                    hobbies = listOf(),
//                ),
//            )
//        given(userService.searchUsers(searchUserDTO))
//            .willReturn(users)
//        mockMvc
//            .perform(
//                post("/api/users/search")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(searchUserDTO)),
//            ).andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(users)))
//    }
//
//    @Test
//    fun `activate user by email`() {
//        val email = "john.doe@example.com"
//        val userDTO =
//            UserDTO(
//                id = 1L,
//                firstName = "John",
//                lastName = "Doe",
//                username = "johndoe",
//                email = email,
//                birthday = "01.01.1973",
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        given(userService.activateUser(email)).willReturn(userDTO)
//        mockMvc
//            .perform(put("/api/users/email/$email/activate"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
//    }
//
//    @Test
//    fun `404 status when activating non-existing user`() {
//        val email = "john.doe@example.com"
//        given(userService.activateUser(email))
//            .willThrow(IllegalArgumentException("User not found"))
//        mockMvc
//            .perform(put("/api/users/email/$email/activate"))
//            .andExpect(status().isNotFound)
//    }
//
//    @Test
//    fun `deactivate user by email`() {
//        val email = "john.doe@example.com"
//        val userDTO =
//            UserDTO(
//                id = 1L,
//                firstName = "John",
//                lastName = "Doe",
//                username = "johndoe",
//                email = email,
//                birthday = "01.01.1973",
//                bio = "Bio",
//                hobbies = listOf(),
//            )
//        given(userService.deactivateUser(email)).willReturn(userDTO)
//        mockMvc
//            .perform(put("/api/users/email/$email/deactivate"))
//            .andExpect(status().isOk)
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
//    }
//
//    @Test
//    fun `404 status when deactivating non-existing user`() {
//        val email = "john.doe@example.com"
//        given(userService.deactivateUser(email))
//            .willThrow(IllegalArgumentException("User not found"))
//        mockMvc
//            .perform(put("/api/users/email/$email/deactivate"))
//            .andExpect(status().isNotFound)
//    }
// }
