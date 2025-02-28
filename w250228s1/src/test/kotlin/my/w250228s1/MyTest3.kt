package my.w250228s1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.netty.channel.ChannelOption
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.support.DefaultPropertySourceFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@TestPropertySource(
    value = ["classpath:test-application.yaml", "classpath:test-application-secret.yaml"],
    factory = DefaultPropertySourceFactory::class,
)
@SpringBootTest
class MyTest3 {

    @Value("\${application.hubspot.url}")
    lateinit var baseUrl: String

    @Value("\${application.hubspot.token}")
    lateinit var apiToken: String


    fun prettyPrintJson(jsonString: String): String {
        val objectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        val jsonNode = objectMapper.readTree(jsonString)
        return objectMapper.writeValueAsString(jsonNode)
    }

    @Test
    fun test3() {
        val httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)

        val client = WebClient.builder()
//            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(baseUrl)
            .defaultHeaders {
                it.add("Authorization", "Bearer $apiToken")
            }.build()

        val result = client.get()
            .uri { builder ->
                builder
                    .path("/crm/v3/objects/companies")
                    .queryParam("properties", "nip,name")
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()!!

//        println(result)
        println(prettyPrintJson(result))
    }
}