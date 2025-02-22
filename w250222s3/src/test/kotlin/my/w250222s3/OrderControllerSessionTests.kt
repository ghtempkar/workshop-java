package my.w250222s3

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerSessionTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    // Funkcja pomocnicza wykonująca logowanie i zwracająca sesję
    private fun performLogin(username: String, password: String): MockHttpSession {
        val loginRequest = mapOf("username" to username, "password" to password)
        val result: MvcResult = mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andReturn()

        // Pobieramy sesję utworzoną przy logowaniu
        return result.request.getSession(false) as MockHttpSession
    }

    // Test: użytkownik z rolą ADMIN loguje się i następnie dodaje zamówienie (POST /orders) – oczekiwany status 200
    @Test
    fun `admin user login and POST orders returns ok`() {
        val session = performLogin("admin", "adminpass")
        val orderRequest = mapOf("description" to "Order from admin")
        mockMvc.perform(
            post("/orders")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        )
            .andExpect(status().isOk)
    }

    // Test: użytkownik z rolą ADMIN loguje się i próbuje pobrać listę zamówień (GET /orders) – oczekiwany status 403
    @Test
    fun `admin user login and GET orders returns forbidden`() {
        val session = performLogin("admin", "adminpass")
        mockMvc.perform(
            get("/orders")
                .session(session)
        )
            .andExpect(status().isForbidden)
    }

    // Test: użytkownik z rolą USER loguje się i pobiera listę zamówień (GET /orders) – oczekiwany status 200
    @Test
    fun `regular user login and GET orders returns ok`() {
        val session = performLogin("user", "userpass")
        mockMvc.perform(
            get("/orders")
                .session(session)
        )
            .andExpect(status().isOk)
    }

    // Test: użytkownik z rolą USER loguje się i próbuje dodać zamówienie (POST /orders) – oczekiwany status 403
    @Test
    fun `regular user login and POST orders returns forbidden`() {
        val session = performLogin("user", "userpass")
        val orderRequest = mapOf("description" to "Order from user")
        mockMvc.perform(
            post("/orders")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        )
            .andExpect(status().isForbidden)
    }

    // Test: niepoprawne logowanie – brak utworzenia sesji, a kolejne wywołanie chronionego endpointu zwraca Unauthorized
    @Test
    fun `invalid login and subsequent GET orders returns unauthorized`() {
        val invalidLoginRequest = mapOf("username" to "wrong", "password" to "wrong")
        mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginRequest))
        )
            .andExpect(status().isForbidden)

        // Brak sesji powoduje, że wywołanie chronionego endpointu nie posiada uwierzytelnienia
        mockMvc.perform(get("/orders"))
            .andExpect(status().isForbidden)
    }
}
