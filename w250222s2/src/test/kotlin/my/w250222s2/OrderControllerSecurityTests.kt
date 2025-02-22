package my.w250222s2

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerSecurityTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    // Test: Użytkownik z rolą ADMIN może dodać zamówienie
    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `POST orders with admin role returns ok`() {
        val orderRequest = mapOf("description" to "Test Order")
        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        )
            .andExpect(status().isOk)
    }

    // Test: Użytkownik z rolą USER nie może dodać zamówienia
    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `POST orders with user role returns forbidden`() {
        val orderRequest = mapOf("description" to "Test Order")
        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        )
            .andExpect(status().isForbidden)
    }

    // Test: Użytkownik z rolą USER może pobrać listę zamówień
    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `GET orders with user role returns ok`() {
        mockMvc.perform(get("/orders"))
            .andExpect(status().isOk)
    }

    // Test: Użytkownik z rolą ADMIN nie ma dostępu do pobierania listy zamówień
    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `GET orders with admin role returns forbidden`() {
        mockMvc.perform(get("/orders"))
            .andExpect(status().isForbidden)
    }

    // Test: Żądania bez uwierzytelnienia powinny zwrócić 401 Unauthorized
    @Test
    fun `GET orders without authentication returns unauthorized`() {
        mockMvc.perform(get("/orders"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `POST orders without authentication returns unauthorized`() {
        val orderRequest = mapOf("description" to "Test Order")
        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        )
            .andExpect(status().isUnauthorized)
    }
}
