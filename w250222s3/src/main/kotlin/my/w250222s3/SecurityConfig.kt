package my.w250222s3

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
class SecurityConfig {

    // In-memory mapa kluczy API do użytkowników.
    // Użytkownik "admin" z rolą ADMIN oraz "user" z rolą USER.
    private val apiKeys = mapOf(
        "admin-key" to UsernamePasswordAuthenticationToken(
            "admin",
            null,
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        ),
        "user-key" to UsernamePasswordAuthenticationToken(
            "user",
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
    )

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .addFilterBefore(ApiKeyAuthFilter(apiKeys), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }
        return http.build()
    }
}

// Filtr sprawdzający klucz API z nagłówka "X-API-KEY"
class ApiKeyAuthFilter(
    private val apiKeys: Map<String, UsernamePasswordAuthenticationToken>
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val apiKey = request.getHeader("X-API-KEY")
        if (apiKey != null) {
            val auth = apiKeys[apiKey]
            if (auth != null) {
                // Ustawiamy autentykację w kontekście bezpieczeństwa
                SecurityContextHolder.getContext().authentication = auth
            } else {
//                logger.warn("Nieprawidłowy API Key: $apiKey")
//                response.status = HttpServletResponse.SC_UNAUTHORIZED
//                return
            }
        } else {
//            logger.warn("Brak API Key w nagłówku")
//            response.status = HttpServletResponse.SC_UNAUTHORIZED
//            return
        }
        filterChain.doFilter(request, response)
    }
}
