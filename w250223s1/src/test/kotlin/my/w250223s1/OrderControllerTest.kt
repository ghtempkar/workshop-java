package my.w250223s1

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [ OrderController::class ],
    excludeAutoConfiguration = [
//        SecurityAutoConfiguration::class,
    ]
)
@Import(
    SecurityConfig::class,
)
class OrderControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun createOrder() {
        mockMvc.perform(
            post("/orders")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(orderRequest))
//                .header("X-API-KEY", "admin-key")
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it)
            }
//            .andExpect(jsonPath("$.id").value(1))
//            .andExpect(jsonPath("$.description").value(description))
    }

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun createOrder2() {
        mockMvc.perform(
            post("/orders")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(orderRequest))
//                .header("X-API-KEY", "admin-key")
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it)
            }
//            .andExpect(jsonPath("$.id").value(1))
//            .andExpect(jsonPath("$.description").value(description))
    }

    @Test
//    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun createOrder3() {
        mockMvc.perform(
            post("/orders")
                .with(httpBasic("admin", "adminpassword"))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(orderRequest))
//                .header("X-API-KEY", "admin-key")
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it)
            }
//            .andExpect(jsonPath("$.id").value(1))
//            .andExpect(jsonPath("$.description").value(description))
    }

    @Test
//    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun createOrder4() {
        mockMvc.perform(
            post("/orders/2")
                .with(httpBasic("admin", "adminpassword"))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(orderRequest))
//                .header("X-API-KEY", "admin-key")
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it)
            }
//            .andExpect(jsonPath("$.id").value(1))
//            .andExpect(jsonPath("$.description").value(description))
    }

}