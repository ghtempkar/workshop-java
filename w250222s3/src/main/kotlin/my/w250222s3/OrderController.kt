package my.w250222s3

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    // Endpoint dostępny tylko dla roli ADMIN – dodawanie zamówienia
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun addOrder(@RequestBody order: Order): ResponseEntity<Order> {
        val savedOrder = orderService.addOrder(order)
        val x = ResponseEntity.ok(savedOrder)
        return x
    }

    // Endpoint dostępny tylko dla roli USER – pobieranie listy zamówień
    //    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    fun getOrders(): ResponseEntity<List<Order>> {
        val x = ResponseEntity.ok(orderService.getAllOrders())
        return x
    }
}
