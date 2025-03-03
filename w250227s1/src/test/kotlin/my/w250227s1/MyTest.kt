package my.w250227s1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.context.ImportTestcontainers
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
internal class MyConfig {

    @Bean
    @DynamicPropertySource
    fun init(registry: DynamicPropertyRegistry) = CommandLineRunner {

        registry.add("my-external.my-port", { 101 })
    }

    @Bean
    @ServiceConnection(name = "nginx")
    fun bla1(): GenericContainer<*> {
        val x = GenericContainer(DockerImageName.parse("nginx:latest"))
            .withExposedPorts(80)

        return x
    }

//    @Bean
//    fun f1(): GenericContainer<*>

}

@SpringBootTest
//@ExtendWith(SpringExtension::class)
//@ImportTestcontainers
@Import(
    MyConfig::class
)
class MyTest {

    @Value("\${my-external.my-port:-1}")
    var myPort: Int = -1

    @Test
    fun test1() {
        println("myPort: $myPort")
        assertThat(myPort).isGreaterThan(0)
    }

}
