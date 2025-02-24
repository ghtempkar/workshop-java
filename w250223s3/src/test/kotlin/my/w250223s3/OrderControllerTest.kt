package my.w250223s3

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OrderController::class)
class OrderControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun test0() {
//        mockMvc.perform(
//            post("/orders")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(orderRequest))
//                .header("X-API-KEY", "admin-key")
//        )
//            .andExpect(status().isOk)
//            .andExpect {
//                println(it)
//            }
//            .andExpect(jsonPath("$.id").value(1))
//            .andExpect(jsonPath("$.description").value(description))
    }

    @Test
    fun test1() {
        mockMvc.perform(
            get("/orders")
                .contentType(MediaType.APPLICATION_JSON)
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
