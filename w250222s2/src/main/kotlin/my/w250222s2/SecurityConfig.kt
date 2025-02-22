package my.w250222s2

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun userDetailsService(): UserDetailsService {
        val inMemoryManager = InMemoryUserDetailsManager()
        val admin = User.withUsername("admin")
            .password(passwordEncoder().encode("adminpass"))
            .roles("ADMIN")
            .build()
        val user = User.withUsername("user")
            .password(passwordEncoder().encode("userpass"))
            .roles("USER")
            .build()
        inMemoryManager.createUser(admin)
        inMemoryManager.createUser(user)
        return inMemoryManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // W przypadku REST API często wyłączamy CSRF – uwaga przy aplikacjach produkcyjnych
            .csrf().disable()
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/login", "/logout").permitAll() // dostęp publiczny
                    .anyRequest().authenticated()
            }
            // Wyłączamy domyślną obsługę form login oraz basic auth, ponieważ tworzymy własne endpointy
            .formLogin().disable()
            .httpBasic().disable()
        return http.build()
    }

    @Bean
    fun authenticationManager(http: HttpSecurity, userDetailsService: UserDetailsService): AuthenticationManager {
        return http.getSharedObject(AuthenticationManagerBuilder::class.java)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build()
    }
}
