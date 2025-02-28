package my.w250228s1

import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.support.DefaultPropertySourceFactory
import org.springframework.web.reactive.function.client.WebClient

@ConfigurationProperties(prefix = "application.hubspot")
data class HubSpotProperties(
    val token: String,
)

@PropertySource(
    value = ["classpath:test-application-secret.yaml"],
    factory = DefaultPropertySourceFactory::class,
)
@EnableConfigurationProperties(HubSpotProperties::class)
@SpringBootTest
class MyTest1 {

    @Value("\${application.hubspot.token}")
    lateinit var apiToken: String

    @Test
    fun test1() {
        val client = WebClient.builder()
            .baseUrl("https://api.hubapi.com")
            .defaultHeaders { headers ->
                headers.add("Authorization", "Bearer ${apiToken}")
            }
            .build()

        val dealId = "7842652615"
        val response = client.get().uri("/crm/v4/objects/deals/$dealId")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println(response)
        println(apiToken)
    }

}