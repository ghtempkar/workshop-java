package my.w250226s2

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class W250226s2ApplicationTests {

	@Test
	fun contextLoads() {
	}

}
