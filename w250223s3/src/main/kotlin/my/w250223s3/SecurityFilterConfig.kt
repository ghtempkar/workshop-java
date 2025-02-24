package my.w250223s3

import java.time.Clock
import my.w250223s3.jwt.FromClaimsJwtUserDetailsResolver
import my.w250223s3.jwt.JwtAuthenticationFilter2
import my.w250223s3.jwt.JwtTokenUtilRS256
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityFilterConfig(
    private val jwtTokenUtil: JwtTokenUtilRS256,
    private val clock: Clock,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(): UserDetailsService {
        // Definicja użytkowników w pamięci
        val user = User.withUsername("user")
            .password("{noop}userpassword")  // {noop} oznacza brak enkodowania – tylko dla celów demonstracyjnych
            .roles("USER")
            .build()

        val user2 = User.withUsername("user2")
            .password("{noop}userpassword")  // {noop} oznacza brak enkodowania – tylko dla celów demonstracyjnych
            .roles("USER", "ADMIN")
            .build()

        val admin = User.withUsername("admin")
            .password("{noop}adminpassword")
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(user2, admin, user)
    }

    @Bean
    fun authenticationManager(
        http: HttpSecurity,
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder,
    ): AuthenticationManager {
        return http.getSharedObject(AuthenticationManagerBuilder::class.java)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
            .and()
            .build()
    }

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter2 =
        JwtAuthenticationFilter2(jwtTokenUtil, FromClaimsJwtUserDetailsResolver(), clock)

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter2
    ): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/**").permitAll() // endpointy logowania/rejestracji publiczne
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

}