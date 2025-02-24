package my.w250223s3

import my.w250223s3.jwt.JwtAuthenticationFilter
import my.w250223s3.jwt.JwtTokenUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityFilterConfig(
    private val jwtTokenUtil: JwtTokenUtil,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity, userDetailsService: UserDetailsService): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/**").permitAll() // endpointy logowania/rejestracji publiczne
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenUtil, userDetailsService), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

}