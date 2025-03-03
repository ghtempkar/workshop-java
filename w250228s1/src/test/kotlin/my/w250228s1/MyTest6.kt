package my.w250228s1

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
class MyTest6 {

    @BeforeEach
    fun beforeEach() {
//        wireMockServer.resetAll()
    }

    @Test
    fun test6() {
        val mockJsonResponse = """
            {
              "results": [
                {
                  "id": "101",
                  "properties": {
                    "name": "Company A",
                    "industry": "Tech"
                  }
                },
                {
                  "id": "102",
                  "properties": {
                    "name": "Company B",
                    "industry": "Finance"
                  }
                }
              ]
            }
        """.trimIndent()

//        val wireMockServer = WireMockExtension.newInstance()
//            .options(options().dynamicPort())
//            .build()

//        wireMockServer.
//        wireMockServer.start()

//        WireMockExtension.newInstance()
//            .options(wireMockConfig().dynamicPort().dynamicHttpsPort())
//            .build();
//
//        wireMockServer.stubFor(
//            get(urlEqualTo("/crm/v3/objects/companies?limit=100"))
//                .willReturn(
//                    aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(mockJsonResponse)
//                        .withStatus(200)
//                )
//        )

        val mockServer = ClientAndServer.startClientAndServer(1080)
        val url = "http://localhost:1080"
        mockServer
            .`when`(
                HttpRequest.request()
                    .withMethod("GET")
                    .withPath("/crm/v3/objects/companies")
                    .withQueryStringParameter("limit", "100")
            )
            .respond(
                HttpResponse.response()
                    .withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockJsonResponse)
            )


        val webClient = WebClient.builder()
            .baseUrl(url)
            .build()

        val apiClient = HubspotApiClient(webClient)

        println("------------uO")
        apiClient.getCompanies()
            .let(::prettyPrintJson)
            .let(::println)
        println("------------uO")

        mockServer.verify(
            HttpRequest.request()
                .withMethod("GET")
                .withPath("/crm/v3/objects/companies")
                .withQueryStringParameter("limit", "100")
        )
    }

//    companion object {
//        @JvmField
//        @RegisterExtension
//        val wireMockServer: WireMockExtension = WireMockExtension.newInstance()
//            .options(com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig().dynamicPort())
//            .build()
//    }
}