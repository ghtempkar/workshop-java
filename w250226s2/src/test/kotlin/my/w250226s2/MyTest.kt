package my.w250226s2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql

@JdbcTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(
    TestcontainersConfiguration::class,
    OrderService::class,
    OrderRepository::class,
)
class MyTest {

    @Autowired
    lateinit var orderService: OrderService

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Test
    @Sql("/test1.sql")
    fun test1() {
        val l = orderRepository.findAll()

        println(l)

        assertThat(l).hasSize(2)
    }

    @Test
    @Sql("/test1.sql")
    fun test1b() {
        val l = orderRepository.findAll()

        println(l)

        assertThat(l).hasSize(2)
    }

    @Test
    fun test3() {
        orderService.addOrder(Order(1, "name1", 10.0))
    }

}