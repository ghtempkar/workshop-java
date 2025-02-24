package my.w250223s3.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.SignatureAlgorithm
import java.time.Clock
import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

internal object JwtAuthControllerIntTestConfig {

    @RestController
    internal class TestController {
        @GetMapping("/test")
        @PreAuthorize("hasRole('ADMIN')")
        fun testEndpoint(): String = "ok"
    }

    @TestConfiguration
    @EnableMethodSecurity
    internal class TestConfig {
        @Bean
        fun clock(): Clock = Clock.systemDefaultZone()

        @Bean
        fun jwtTokenCoder(): JwtTokenCoder = JwtTokenCoder.ofHS512()

        @Bean
        fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

        @Bean
        fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
            // Definicja użytkowników w pamięci
            val user = User.withUsername("user")
//            .password("{noop}userpassword")  // {noop} oznacza brak enkodowania – tylko dla celów demonstracyjnych
                .password(passwordEncoder.encode("userpassword"))
                .roles("USER")
                .build()

            val user2 = User.withUsername("user2")
                .password(passwordEncoder.encode("userpassword"))
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
//        http.getSharedObject(AuthenticationManagerBuilder::class.java).apply {
//            userDetailsService(userDetailsService)
//            passwordEncoder(passwordEncoder)
//        }.build()

            return http.getSharedObject(AuthenticationManagerBuilder::class.java)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build()
        }

        @Bean
        fun jwtAuthenticationFilter(
            jwtTokenUtil: JwtTokenCoder,
            userDetailsService: UserDetailsService,
            clock: Clock
        ): JwtAuthenticationFilter {
            return JwtAuthenticationFilter(jwtTokenUtil, FromClaimsJwtUserDetailsResolver(), clock)
        }

        @Bean
        fun securityFilterChain(
            http: HttpSecurity,
            jwtAuthenticationFilter: JwtAuthenticationFilter
        ): SecurityFilterChain {
            http.csrf { it.disable() }
                .authorizeHttpRequests {
                    it.requestMatchers("/auth/login").permitAll()
//                it.requestMatchers("/auth/**").permitAll() // endpointy logowania/rejestracji publiczne
                    it.anyRequest().authenticated()
                }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter::class.java
                )
            return http.build()
        }
    }

}

@WebMvcTest(
    controllers = [
        JwtAuthController::class,
        JwtAuthControllerIntTestConfig.TestController::class,
    ],
)
@Import(
    JwtAuthControllerIntTestConfig.TestConfig::class,
)
internal class JwtAuthControllerIntTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun test_login() {
        val loginRequest = JwtAuthController.AuthRequest(
//            username = "user",
            username = "user2",
            password = "userpassword",
        )

        val r = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer $token")
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
//            .andExpect {
//                println(it)
//            }
            .andExpect(jsonPath("$.token", not(emptyOrNullString())))
        println(r)

        val resp: JwtAuthController.AuthResponse = objectMapper.readValue(r.andReturn().response.contentAsString)
        println(resp)

        val token =resp.token
        mockMvc.perform(
            get("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect { content().string("ok") }
    }

}