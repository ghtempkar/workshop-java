package my.w250223s3.jwt

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureAlgorithm.HS512
import java.time.Clock
import java.time.Duration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

internal object JwtTestConfig {

    @RestController
    internal class TestController {
        @GetMapping("/test")
        @PreAuthorize("hasRole('ADMIN')")
        fun testEndpoint(): String = "ok"
    }

    @TestConfiguration
    @EnableMethodSecurity
    internal class TestConfig(
//        private val jwtTokenUtil: JwtTokenUtilInt = JwtTokenUtil2(),
    ) {

        @Bean
        fun clock(): Clock = Clock.systemDefaultZone()

        @Bean
        fun jwtTokenUtil(clock: Clock): JwtTokenCoder = JwtTokenCoder.ofHS512(JwtTokenCoder.generateKey(SignatureAlgorithm.HS512))

        @Bean
        fun userDetailsService(passwordEncoder: PasswordEncoder): InMemoryUserDetailsManager {
            val user = User.withUsername("myuser")
                .password(passwordEncoder.encode("userpass"))
                .roles("USER")
                .build()
            val admin = User.withUsername("myadmin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build()
            return InMemoryUserDetailsManager(user, admin)
        }

        @Bean
        fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

        @Bean
        fun jwtAuthenticationFilter(jwtTokenUtil: JwtTokenCoder, userDetailsService: UserDetailsService, clock: Clock): JwtAuthenticationFilter {
            return JwtAuthenticationFilter(jwtTokenUtil, FromClaimsJwtUserDetailsResolver(), clock)
        }

        @Bean
        fun securityFilterChain(http: HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter): SecurityFilterChain {
            http.csrf { it.disable() }
                .authorizeHttpRequests {
//                it.requestMatchers("/auth/**").permitAll() // endpointy logowania/rejestracji publiczne
                    it.anyRequest().authenticated()
                }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter::class.java,
//                    AuthorizationFilter::class.java,
                )
            return http.build()
        }
    }

}

@WebMvcTest(
    controllers = [JwtTestConfig. TestController::class],
//    excludeFilters = [@ComponentScan.Filter JwtTokenUtil2::class],
    excludeAutoConfiguration = [ImportAutoConfiguration::class],
)
@Import(
    JwtTestConfig.TestConfig::class,
)
internal class JwtAuthenticationFilterIntTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenCoder



    @Test
    fun testJwt() {
        val clock = Clock.systemDefaultZone()

        val token = jwtTokenUtil.generateToken(
            User("myadmin", "adminpass", listOf(SimpleGrantedAuthority("ROLE_ADMIN"))),
            clock,
            Duration.ofMinutes(1),
        )
//        val token = jwtTokenUtil.generateToken(User("myuser", "userpass", listOf(SimpleGrantedAuthority("USER"))))

        mockMvc.perform(
            get("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect { content().string("ok") }
    }

    @Test
    fun testJwt3() {

        val util2 = JwtTokenCoder.ofHS512() // JwtTokenUtilHS512(clock = Clock.systemDefaultZone()) // diffrent secret

        io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS512)

        val clock = Clock.systemDefaultZone()
        val token = util2.generateToken(
            User("myadmin", "adminpass", listOf(SimpleGrantedAuthority("ADMIN"))),
            clock,
            Duration.ofMinutes(1),
            )
//        val token = jwtTokenUtil.generateToken(User("myuser", "userpass", listOf(SimpleGrantedAuthority("USER"))))

        mockMvc.perform(
            get("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isForbidden)
//            .andExpect { content().string("ok") }
    }

    @Test
//    @WithMockUser[]
    fun testJwt2() {
        val clock = Clock.systemDefaultZone()
        val token = jwtTokenUtil.generateToken(
            User("myuser", "userpass", listOf(SimpleGrantedAuthority("USER"))),
            clock,
            Duration.ofMinutes(1),
        )

        mockMvc.perform(
            get("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isForbidden)
//            .andExpect { content().string("ok") }
    }

}