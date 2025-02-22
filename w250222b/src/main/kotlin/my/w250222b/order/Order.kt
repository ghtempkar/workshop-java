package my.w250222b.order

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
) {

    @PostMapping
    fun createOrder(@RequestBody @Valid order: Order) {
        orderService.add(order)
    }

    @GetMapping
    @ResponseBody
    fun listOrders(): List<Order> {
        return orderService.list()
    }

}

data class Order(
    @field:NotBlank
    val name: String,
    @field:Min(1) @field:Max(5)
    val amount: Int,
)

@Service
class OrderService(

) {

    private val orders: MutableList<Order> = mutableListOf()

    fun add(order: Order) {
        orders.add(order)
    }

    fun list(): List<Order> = orders.toList()

}