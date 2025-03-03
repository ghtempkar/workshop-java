package my.w250228s1

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import my.w250228s1.HubspotApiClient.SearchDealsRequest.Filter
import my.w250228s1.HubspotApiClient.SearchDealsRequest.FilterGroup
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

class HubspotApiClient(private val webClient: WebClient) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private data class SearchDealsRequest(
        @JsonProperty("filterGroups") val filterGroups: List<FilterGroup>,
        @JsonProperty("properties") val properties: List<String>,
        @JsonProperty("sorts") val sorts: List<String>? = null,
        @JsonProperty("limit") val limit: Int,
        @JsonProperty("after") val after: Int
    ) {

        data class FilterGroup(
            @JsonProperty("filters") val filters: List<Filter>
        )

        data class Filter(
            @JsonProperty("value") val value: String,
            @JsonProperty("propertyName") val propertyName: String,
            @JsonProperty("operator") val operator: String
        )
    }

    fun searchDeals(): String {
        val request = SearchDealsRequest(
            filterGroups = listOf(
                FilterGroup(
                    listOf(
                        Filter(
                            value = "121958090",
                            propertyName = "dealstage",
                            operator = "EQ",
                        )
                    )
                ),
            ),
            properties = listOf(
                "dealname",
                "nazwa_firmy",
                "createdate",
                "hs_lastmodifieddate",
                "data_zlozenia_zamowienia_przez_klienta",
            ),
            sorts = listOf("-createdate"),
            limit = 100,
            after = 0,
        )

        val objectMapper = jacksonObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)

        val result = webClient.post()
            .uri { builder ->
                builder.path("/crm/v3/objects/deals/search")
                    .build()
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(request))
            .retrieve()
            .bodyToMono(String::class.java)
            .block() !! // todo:

        return result
    }

    fun getCompany(companyId: String): String {
        val result = webClient.get(
            "/crm/v3/objects/companies/${companyId}",
            properties = listOf(
                "nip", "termin_platnosci", "address", "zip", "city",
                "companyinformation", "adres_do_korespondencji", "adres_dostawy", "adres_dostawy_2", "branza_navireo",
                "calkowity_limit_kredytowy_", "dostawcy_klienta", "e_faktury_kontakt__e_mail_", "e_mail__ogolny_",
            )
        )
        return result
    }

    fun getCompanies(after: String? = null): String {
//        val objectMapper = jacksonObjectMapper()
//            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return webClient.get(
            "/crm/v3/objects/companies",
            uriBuilderBlock = {
                queryParam("limit", "100")
//                queryParam("sort", "createdAt")
//                after?.let { queryParam("after", it) }
            }
        )
//            .let {
//            objectMapper.readValue(it, HubSpotProductsResponse::class.java)
//        }
    }


    fun getLineItem(lineItemId: String): String {
        val result = webClient.get(
            "/crm/v3/objects/line_items/${lineItemId}",
            properties = listOf(
                "rodzaj_opakowania", "numer_specyfikacji__sklonowano_", "quantity", "price",
                "hs_line_item_currency_code", "hs_product_id", "amount", "cena_produktu", "createdate", "description",
                "discount", "hs_discount_percentage", "name", "nazwa", "nazwa_angielska", "numer_specyfikacji",
                "numer_specyfikacji__do_wpisywania_recznego_", "numer_specyfikacji__sklonowano_", "price", "quantity",
                "recurringbillingfrequency", // "rodzaj_opakowania",
                "rodzaj_opakowania__do_wyceny_w_j__angielskim_", "singiel___etanol", "slownik_produktow", "tax",
                "z_zaplacona_akcyza",
                "kod_dostawcy",
            )
        )
        return result
    }

    fun getContract(contractId: String): String {
        val result = webClient.get(
            "/crm/v3/objects/contacts/${contractId}",
            properties = listOf(
                "ip_city",
                "ip_country",
                "ip_country_code",
                "ip_latlon",
                "ip_state",
                "ip_state_code",
                "ip_zipcode",
                "job_function",
                "firstname",
                "lastname",
                "mobilephone",
                "phone",
                "address",
                "state",
                "zip",
                "country",
                "company",
            ),
        )
        return result
    }

    fun getOwner(ownerId: String, archived: Boolean): String {
        val result = webClient.get(
            "/crm/v3/owners/${ownerId}",
            uriBuilderBlock = if (archived) {
                {
                    queryParam("idProperty", "id")
                    queryParam("archived", "true")
                }
            } else {
                null
            },
        )
        return result
    }

    fun getOwnerWithArchived(ownerId: String): String {
        return getOwner(ownerId, false) // todo: !!!
    }

    fun getDeal(dealId: String): String {
        return webClient.get(
            "/crm/v3/objects/deals/${dealId}",
            associations = listOf(
                "contacts", "companies", "line_items"
            ),
            properties = listOf(
                "createdate",
                "dealname", // nazwa transakcji
                "hubspot_owner_id", // wlasciciel transakcji
                "nazwa_firmy", // nazwa firmy
                "adres_dostawy", // adres dostawy
                "wymagany_termin_dostawy", // todo: czy to jest "wymagany termin dostawy do klienta" ?
                "data_zlozenia_zamowienia_przez_klienta",
                "numer_zamowienia_klienta", // oryginalny numer zamowienia klienta
                "rodzaj_opakowania", // rodzaj opakowania
                "termin_platnosci",
                "potwierdzenie_terminu_dostawy_na_adres",
                "sprzedaz_opakowan_z_produktem",

                "wlasciciel_transakcji_sprzedazy",

                "uwagi_dla_dz__realizacji__t_",
                "uwagi_dla_magazynu__t_",
                "uwagi_dla_dz__jakosci__t_",

                "termin_wypozyczenia_opakowan",
                "pro_forma",
                "nip",
                "kod_dostawcy",
            ),
        )
    }

    fun getDealWithAssociations(dealId: String): String {
        return this.getDeal(dealId)
    }

    fun getProduct(productId: String): String {
        return webClient.get(
            "/crm/v3/objects/products/${productId}",
        )
    }


    data class HubSpotProductsResponse(
        @JsonProperty("results") val results: List<Product>,
        @JsonProperty("paging") val paging: Paging? = null
    ) {
        data class Product(
            @JsonProperty("id") val id: String,
            @JsonProperty("properties") val properties: Map<String, String?>,
            @JsonProperty("createdAt") val createdAt: String?,
            @JsonProperty("updatedAt") val updatedAt: String?,
            @JsonProperty("archived") val archived: String? = null,
        )

        data class ProductProperties(
            @JsonProperty("name") val name: String?,
            @JsonProperty("price") val price: String?,
            @JsonProperty("createdAt") val createdAt: String?,
            @JsonProperty("updatedAt") val updatedAt: String?
        )

        data class Paging(
            @JsonProperty("next") val next: NextPage? = null,
            @JsonProperty("link") val link: String? = null,
        )

        data class NextPage(
            @JsonProperty("after") val after: String,
            @JsonProperty("link") val link: String? = null,
        )
    }

    fun getProducts(after: String? = null): HubSpotProductsResponse {
        val objectMapper = jacksonObjectMapper()
//            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return webClient.get(
            "/crm/v3/objects/products",
            uriBuilderBlock = {
                queryParam("limit", "100")
                queryParam("sort", "createdAt")
                after?.let { queryParam("after", it) }
            }
        ).let {
            objectMapper.readValue(it, HubSpotProductsResponse::class.java)
        }
    }

    private fun WebClient.get(
        url: String,
        associations: List<String>? = null,
        properties: List<String>? = null,
        uriBuilderBlock: (UriBuilder.() -> Unit)? = null,
    ): String {
        return this
            .get()
            .uri { builder ->
                builder
                    .path(url)
                    .apply {
                        if (!associations.isNullOrEmpty()) {
                            queryParam("includeAssociations", "true")
                            queryParam("associations", associations.joinToString(","))
                        }
                    }
                    .apply {
                        if (!properties.isNullOrEmpty()) {
                            queryParam("properties", properties.joinToString(","))
                        }
                    }
                    .apply {
                        if (uriBuilderBlock != null) {
                            uriBuilderBlock(builder)
                        }
                    }
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
            .let(::requireNotNull)
    }

}