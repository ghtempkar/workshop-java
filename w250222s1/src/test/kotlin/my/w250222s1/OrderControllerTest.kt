package my.w250222s1

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `admin can add order`() {
        mockMvc.perform(
            post("/orders")
                .with(httpBasic("admin", "adminpassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"description": "Test order"}""")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.description").value("Test order"))
    }

    @Test
    fun `user cannot add order`() {
        mockMvc.perform(
            post("/orders")
                .with(httpBasic("user", "userpassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"description": "Test order"}""")
        )
            .andExpect(status().isForbidden())
    }

    @Test
    fun `user can get orders`() {
//        // Najpierw dodajemy zamówienie używając konta admina
//        mockMvc.perform(
//            post("/orders")
//                .with(httpBasic("admin", "adminpassword"))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("""{"description": "Test order for get"}""")
//        )
//            .andExpect(status().isOk())

        // Następnie pobieramy zamówienia przy użyciu konta usera
        mockMvc.perform(
            get("/orders")
                .with(httpBasic("user", "userpassword"))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].description").value("Test order for get"))
    }

    @Test
    fun `admin cannot get orders`() {
        mockMvc.perform(
            get("/orders")
                .with(httpBasic("admin", "adminpassword"))
        )
            .andExpect(status().isForbidden())
    }

    @Test
    fun `unauthenticated request is unauthorized`() {
        mockMvc.perform(get("/orders"))
            .andExpect(status().isUnauthorized())
    }
}
