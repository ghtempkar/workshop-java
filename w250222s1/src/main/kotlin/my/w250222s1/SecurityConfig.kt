package my.w250222s1

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity  // Włącza zabezpieczenia metod (@PreAuthorize)
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {


        http.csrf(Customizer. withDefaults()) // Dla prostoty wyłączamy CSRF – w produkcji warto rozważyć odpowiednią konfigurację
            .authorizeHttpRequests { requests ->
                requests.anyRequest().authenticated()
            }
            .httpBasic(Customizer.withDefaults()) // Używamy HTTP Basic auth
        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        // Definicja użytkowników w pamięci
        val user = User.withUsername("user")
            .password("{noop}userpassword")  // {noop} oznacza brak enkodowania – tylko dla celów demonstracyjnych
            .roles("USER")
            .build()

        val admin = User.withUsername("admin")
            .password("{noop}adminpassword")
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(user, admin)
    }
}
