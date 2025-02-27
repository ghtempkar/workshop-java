package my.w250226s4

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject

@Import(TestcontainersConfiguration::class)
@JdbcTest
class MyTest {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun test1() {
        val s = jdbcTemplate.queryForObject<String>("SELECT CURRENT_TIME")
        println(s)
        assertThat(s).isNotBlank()
    }
}