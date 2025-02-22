package my.w250222.orders

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


data class Order(
    val id: Long? = null,
    @field:NotBlank
    val customerName: String? = null,
    val items: List<String>,
    val totalPrice: Double
)

@RestController
@RequestMapping("/orders")
class OrdersController {

    private val orders = mutableListOf<Order>()

    @PostMapping
    @ResponseBody
    fun createOrder(@Valid @RequestBody order: Order): Order {
        val newOrder = order.copy(id = (orders.size + 1).toLong())
        orders.add(newOrder)
        return newOrder
    }

    @GetMapping
    @ResponseBody
    fun getOrders(): List<Order> = orders



}