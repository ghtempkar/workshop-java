package my.w250224s1

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
//@OpenAPIDefinition
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Order Service API")
                .version("1.0")
                .description("API do składania zleceń")
        )
        .components(
            Components().addSecuritySchemes("basicAuth",
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")
            )
        )
}
