package my.w250228s1

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.support.DefaultPropertySourceFactory
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

data class Deal(
    val id: String,
    val properties: Properties,
    @JsonProperty("createdAt") val createdAt: String,
    @JsonProperty("updatedAt") val updatedAt: String,
    val archived: Boolean
) {
    data class Properties(
        val amount: String,
        @JsonProperty("closedate") val closeDate: String,
        @JsonProperty("createdate") val createDate: String,
        val dealname: String,
        val dealstage: String,
        @JsonProperty("hs_lastmodifieddate") val lastModifiedDate: String,
        @JsonProperty("hs_object_id") val objectId: String,
        val pipeline: String
    )
}

@SpringBootTest
@TestPropertySource(
    value = [
        "classpath:test-application.yaml",
        "classpath:test-application-secret.yaml",
    ],
    factory = DefaultPropertySourceFactory::class,
)
class MyTest2 {

    @Value("\${application.hubspot.url}")
    lateinit var baseUrl: String

    @Value("\${application.hubspot.token}")
    lateinit var apiToken: String

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun test1() {
        println(baseUrl)

        val client = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeaders { headers ->
                headers.add("Authorization", "Bearer $apiToken")
            }
            .build()

        val dealId = "7842652615"
        val response = client.get().uri("/crm/v4/objects/deals/$dealId")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        println(response)
        val deal: Deal = objectMapper.readValue(response, Deal::class.java)

        println(deal)
    }

}