package my.w250223s3.p

import io.jsonwebtoken.SignatureAlgorithm
import my.w250223s3.JwtAuthenticationFilter
import my.w250223s3.JwtTokenUtil2
import my.w250223s3.JwtTokenUtilInt
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TestController {
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    fun testEndpoint(): String = "ok"
}

@TestConfiguration
@EnableMethodSecurity
internal class TestConfig(
    private val jwtTokenUtil: JwtTokenUtilInt = JwtTokenUtil2(),
) {

    @Bean
    fun jwtTokenUtil(): JwtTokenUtilInt =
        JwtTokenUtil2()

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
    fun securityFilterChain(http: HttpSecurity, userDetailsService: UserDetailsService): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
//                it.requestMatchers("/auth/**").permitAll() // endpointy logowania/rejestracji publiczne
                    it.anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenUtil, userDetailsService),
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }
}

@WebMvcTest(
    controllers = [TestController::class],
//    excludeFilters = [@ComponentScan.Filter JwtTokenUtil2::class],
    excludeAutoConfiguration = [ImportAutoConfiguration::class],
)
@Import(
    TestConfig::class,
)
internal class JwtAuthenticationFilterIntTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtilInt

    @Test
    fun testJwt() {


        io.jsonwebtoken.security.Keys.secretKeyFor(SignatureAlgorithm.HS512)
//        val token = jwtTokenUtil.generateToken(User("myadmin", "adminpass", listOf(SimpleGrantedAuthority("ADMIN"))))
        val token = jwtTokenUtil.generateToken(User("myuser", "userpass", listOf(SimpleGrantedAuthority("USER"))))

        mockMvc.perform(
            get("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
//            .andExpect { content().string("pong") }
    }

}