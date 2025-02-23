package my.w250222s3

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


//@ExtendWith(MockitoExtension::class)
//@ExtendWith(SpringExtension::class)
@WebMvcTest(OrderController::class)
@Import(
    SecurityConfig::class,
    OrderService::class,
)
class OrderControllerTest {

    @Autowired  lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var orderService: OrderService
//    @Mock  lateinit var orderService: OrderService

    private val objectMapper = ObjectMapper()

    @Test
    fun `should add order when user has ADMIN role`() {
        val description = "Test order"
        val order = Order(1, description)
        given(orderService.addOrder(order)).willReturn(order)

        val orderRequest = mapOf("description" to description)

        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
                .header("X-API-KEY", "admin-key")
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it)
            }
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.description").value(description))
    }

    @Test
    fun `should return unauthorized when adding order with missing API key`() {
        val orderRequest = mapOf("description" to "Test order")
        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `should get orders when user has USER role`() {
        val orders = listOf(Order(1, "Order 1"), Order(2, "Order 2"))
        given(orderService.getAllOrders()).willReturn(orders)

        mockMvc.perform(
            get("/orders")
                .header("X-API-KEY", "user-key")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].description").value("Order 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].description").value("Order 2"))
    }

    @Test
    fun `should return unauthorized when getting orders with missing API key`() {
        mockMvc.perform(get("/orders"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `should return forbidden when using wrong role for endpoint`() {
        // Próba pobrania zamówień przy użyciu klucza admin-key, który nadaje rolę ADMIN,
        // natomiast endpoint wymaga ROLE_USER.
        mockMvc.perform(
            get("/orders")
                .header("X-API-KEY", "admin-key")
        )
            .andExpect(status().isForbidden)
    }
}
