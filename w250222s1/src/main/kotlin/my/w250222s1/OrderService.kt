package my.w250222s1

import org.springframework.stereotype.Service

@Service
class OrderService {
    private val orders = mutableListOf<Order>()
    private var nextId = 1L

    init {
        orders.add(Order(
            0, "Test order for get"
        ))
    }

    fun addOrder(order: Order): Order {
        // nadanie unikalnego identyfikatora zamówieniu
        val newOrder = order.copy(id = nextId++)
        orders.add(newOrder)
        return newOrder
    }

    fun getAllOrders(): List<Order> = orders
}
