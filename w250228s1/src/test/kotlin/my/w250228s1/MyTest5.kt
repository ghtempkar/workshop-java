package my.w250228s1

import my.w250228s1.HubspotApiClient.HubSpotProductsResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.support.DefaultPropertySourceFactory
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

@SpringBootTest
@TestPropertySource(
    value = ["classpath:test-application.yaml", "classpath:test-application-secret.yaml"],
    factory = DefaultPropertySourceFactory::class,
)
class MyTest5 {

    @Value("\${application.hubspot.token}")
    lateinit var token: String

    @Value("\${application.hubspot.url}")
    lateinit var url: String

    @Test
    fun test5() {
        val client = WebClient.builder()
            .baseUrl(url)
            .defaultHeaders { headers -> headers.add("Authorization", "Bearer $token") }
            .build()


//        val companyId = "6479836625"
//        HubspotApiClient(client).getContract(companyId)
//            .let(::prettyPrintJson)

        val dealId = "141989617880"
        HubspotApiClient(client).getDealWithAssociations(dealId)
            .let(::prettyPrintJson)
            .let(::println)

        val lineItem = "108505443532"
        HubspotApiClient(client).getLineItem(lineItem)
            .let(::prettyPrintJson)
            .let(::println)

        val productId = "1096081614"
        HubspotApiClient(client).getProduct(productId)
            .let(::prettyPrintJson)
            .let(::println)

        val allProducts = mutableListOf< HubSpotProductsResponse.Product>()
        var after: String? = null
        for (i in 0..20) {
            val r = HubspotApiClient(client).getProducts(after)
//            .let(::prettyPrintJson)
//                .also(::println)

            r.results.forEach { product ->
//                println("${product.id} - ${product.properties["name"]}");
                allProducts.add(product)
            }
            println("---> count: ${r.results.count()} , after: $after")

            after = r.paging?.next?.after

            if (i > 0 && after == null) {
                break
            }
        }

        val allProductsSorted = allProducts.sortBy { it.properties["name"] }
//        allProductsSorted.
        allProducts.forEach { product ->
            println("${product.id} - ${product.properties["name"]}");
        }
    }

    @Test
    fun test5_2() {
        val client = WebClient.builder()
            .baseUrl(url)
            .defaultHeaders { headers -> headers.add("Authorization", "Bearer $token") }
            .build()

        val result = HubspotApiClient(client).searchDeals()
        println(prettyPrintJson(result))
    }
}