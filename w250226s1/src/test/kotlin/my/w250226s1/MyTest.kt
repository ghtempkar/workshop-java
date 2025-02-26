package my.w250226s1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql

@JdbcTest
@Import(
    OrderRepository::class,
)
class MyTest {

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
}