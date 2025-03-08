package my.w250227s3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject

@TestConfiguration
@AutoConfigureJdbc
@Import(TestcontainersConfiguration::class)
internal class MyConfig {

}

@SpringBootTest
@Import(
    MyConfig::class,
)
class MyTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun test1() {
        val s = jdbcTemplate.queryForObject<String>("select CURRENT_TIME")
        println(s)
        assertThat(s).isNotBlank()
    }

    @Value("\${my-value: XXX}")
    lateinit var myValue: String

    @Test
    fun test3() {
        println(myValue)
        println()
        assertThat(myValue).isEqualTo("dynamic-value")
    }

    @Test
    fun test2(
        @Value("\${my-value}") myValue: String,
        @Value("\${my-value2}") myValue2: Int,
    ) {
        println(myValue)
        println(myValue2)
        println()
    }
}