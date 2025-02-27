package my.w250227s1

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class W250227s1ApplicationTests {

    @Test
    fun contextLoads() {
    }

}
