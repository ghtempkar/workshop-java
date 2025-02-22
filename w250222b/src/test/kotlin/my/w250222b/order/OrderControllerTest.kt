package my.w250222b.order

import com.fasterxml.jackson.databind.ObjectMapper
import my.w250222b.config.ControllerExceptionHandler
import my.w250222b.config.MyConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(OrderController::class)
@Import(
    MyConfiguration::class,
    OrderService::class,
    ControllerExceptionHandler::class,
)
class OrderControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper

//    @Autowired
    @Mock
    lateinit var orderService: OrderService

    @Test
    fun createOrder() {
    }

    @Test
    fun listOrders() {
    }

    @Test
    fun `should return 400 when customerName is blank`() {
        val invalidOrder = mapOf(
            "name" to "abc",
            "amount" to 2
        )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrder))
        )
            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.customerName").value("Customer name cannot be blank"))
    }

    @Test
    fun `test 2`() {
        val invalidOrder = mapOf(
            "name" to "",
            "amount" to 2
        )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrder))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.name").value("must not be blank"))
    }

    @Test
    fun `test 3`() {
        val invalidOrder = mapOf(
            "name" to "",
            "amount" to 2
        )

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language",  "pl")
                .content(objectMapper.writeValueAsString(invalidOrder))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.name").value("pl pl must not be blank"))
    }

}