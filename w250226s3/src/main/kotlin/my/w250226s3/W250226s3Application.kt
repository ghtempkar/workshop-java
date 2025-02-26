package my.w250226s3

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@SpringBootApplication
class W250226s3Application

@Configuration
class MyConfig {

    @Bean
    fun init1(orderService: OrderService) = CommandLineRunner {
        orderService.addOrder(Order(id = 1, customerName = "aa", totalPrice = 11.0))
    }

}

data class Order(
    val id: Long? = null,
    val customerName: String,
    val totalPrice: Double
)

@Repository
class OrderRepository(
    private val jdbcTemplate: JdbcTemplate,
) {
    private val rowMapper = RowMapper { rs, _ ->
        Order(
            id = rs.getLong("id"),
            customerName = rs.getString("customer_name"),
            totalPrice = rs.getDouble("total_price")
        )
    }

    fun findAll(): List<Order> =
        jdbcTemplate.query("SELECT id, customer_name, total_price FROM orders", rowMapper)

    fun save(order: Order): Order {
        jdbcTemplate.update(
            "insert into orders (customer_name, total_price) values (?, ?)",
            order.customerName, order.totalPrice,
        )
        return order
    }
}

@Service
class OrderService(private val orderRepository: OrderRepository) {

    fun getOrders(): List<Order> = orderRepository.findAll()

    fun addOrder(order: Order): Order = orderRepository.save(order)
}

@Service
class MyService {

}

fun main(args: Array<String>) {
    runApplication<W250226s3Application>(*args)
}
