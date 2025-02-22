package my.w250222s1

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
        return ResponseEntity.ok(savedOrder)
    }

    // Endpoint dostępny tylko dla roli USER – pobieranie listy zamówień
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    fun getOrders(): ResponseEntity<List<Order>> {
        return ResponseEntity.ok(orderService.getAllOrders())
    }
}
