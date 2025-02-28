package my.w250227s3

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.core.env.StandardEnvironment
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:17.4"))
    }

    @Bean
    fun nginxContainer(): GenericContainer<*> {
        return GenericContainer(DockerImageName.parse("nginx:latest"))
            .withExposedPorts(80)
    }

    @Bean
    fun configureEnvironment(
        env: Environment,
        nginxContainer: GenericContainer<*>,
    ): Environment {
        (env as StandardEnvironment).propertySources.addFirst(
            org.springframework.core.env.MapPropertySource(
                "customProperties",
                mapOf(
                    "my-value" to "dynamic-value",
                    "my-value2" to nginxContainer.firstMappedPort,
                ),
            )
        )
        return env
    }

}
