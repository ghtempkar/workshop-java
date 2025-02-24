```kotlin
package com.example.order.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val securitySchemeName = "basicAuth"
        return OpenAPI()
            .info(
                Info()
                    .title("Order API")
                    .description("API zabezpieczone Basic Auth. Każdy endpoint wymaga uwierzytelnienia, poza /public/echo.")
                    .version("1.0")
            )
            .components(
                Components().addSecuritySchemes(securitySchemeName,
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                )
            )
            // Ustawienie globalnego wymogu Basic Auth dla wszystkich endpointów
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
    }
}

```

---

```kotlin
package com.example.order.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public")
class PublicController {

    @Operation(
        summary = "Publiczny echo endpoint",
        description = "Endpoint dostępny bez uwierzytelnienia",
        security = [] // wyłączenie wymogu Basic Auth w dokumentacji
    )
    @GetMapping("/echo")
    fun echo(): ResponseEntity<String> {
        return ResponseEntity.ok("Public Echo!")
    }
}

```

---

```kotlin
package com.example.order.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController {

    @Operation(
        summary = "Zabezpieczony endpoint",
        security = [SecurityRequirement(name = "basicAuth")]
    )
    @GetMapping
    fun getOrders(): ResponseEntity<String> {
        return ResponseEntity.ok("To jest endpoint wymagający Basic Auth.")
    }
}

```