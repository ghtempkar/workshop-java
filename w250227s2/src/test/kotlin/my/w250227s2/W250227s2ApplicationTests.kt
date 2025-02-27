package my.w250227s2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@TestConfiguration
internal class MyConfig {



//    @Bean
//    fun init0(r: DynamicPropertyRegistry): String {
//        println("init0")
//        return ""
//    }

////    @Bean
//    @DynamicPropertySource
//    fun init(r: DynamicPropertyRegistry) {
//        r.add("my-value") {
//            "AAA"
//        }
//    }

}

@SpringBootTest
@Import(
    MyConfig::class,
)
class W250227s2ApplicationTests {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("my-value") { "AAA" }
        }
    }

    @Test
    fun contextLoads() {
    }

    @Test
    fun test2(@Value("\${my-value:abc}") myValue: String) {
        println("my-value: $myValue")
        assertThat(myValue).isEqualTo("AAA")
    }

}
