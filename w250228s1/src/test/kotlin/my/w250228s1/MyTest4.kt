package my.w250228s1

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.support.DefaultPropertySourceFactory
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@TestPropertySource(
    value = ["classpath:test-application.yaml", "classpath:test-application-secret.yaml"],
    factory = DefaultPropertySourceFactory::class,
)
class MyTest4 {

    @Value("\${application.hubspot.url}")
    lateinit var url: String

    @Value("\${application.hubspot.token}")
    lateinit var apiToken: String

    @Test
    fun test4() {
        val companyId = "6479836625"

        val client = WebClient.builder()
            .baseUrl(url)
            .defaultHeaders { headers -> headers.add("Authorization", "Bearer $apiToken") }
            .build()

        val result = client.get()
            .uri { builder ->
                builder
                    .path("/crm/v3/associations/companies/${companyId}/deals")
                    .queryParam("limit", 100)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()!!

        println(prettyPrintJson(result))
    }
}