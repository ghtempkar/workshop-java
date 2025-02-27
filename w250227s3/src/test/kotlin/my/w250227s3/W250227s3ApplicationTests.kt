package my.w250227s3

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class W250227s3ApplicationTests {

    @Test
    fun contextLoads() {
    }

}
