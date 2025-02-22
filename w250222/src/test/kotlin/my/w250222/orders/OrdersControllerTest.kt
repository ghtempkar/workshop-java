package my.w250222.orders

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OrdersController::class)
class OrdersControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
) {

//    private val objectMapper = jacksonObjectMapper()

    @Test
    fun createOrder() {

        val order = Order(
            customerName = "Jan Kowalski",
            items = listOf("item1", "item2"),
            totalPrice = 100.0
        )
        val orderJson = objectMapper.writeValueAsString(order)

        // Wykonanie POST /orders
        val result = mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderJson))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.customerName").value("Jan Kowalski"))
            .andExpect(jsonPath("$.items[0]").value("item1"))
            .andExpect(jsonPath("$.totalPrice").value(100.0))
            .andReturn()

        // Opcjonalne sprawdzenie sparsowanego wyniku
        val responseOrder: Order = objectMapper.readValue(result.response.contentAsString)
        assert(responseOrder.id != null)

    }

    @Test
    fun createOrder2() {

        val order = Order(
            customerName = "",
            items = listOf("item1", "item2"),
            totalPrice = 100.0
        )
        val orderJson = objectMapper.writeValueAsString(order)

        // Wykonanie POST /orders
        val result = mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderJson))
//            .andExpect { it ->
//                println(it)
////                true
//            }
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.customerName").value("Jan Kowalski"))
            .andExpect(jsonPath("$.items[0]").value("item1"))
            .andExpect(jsonPath("$.totalPrice").value(100.0))
            .andReturn()

        // Opcjonalne sprawdzenie sparsowanego wyniku
        val responseOrder: Order = objectMapper.readValue(result.response.contentAsString)
        assert(responseOrder.id != null)

    }


}