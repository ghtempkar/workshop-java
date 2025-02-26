package my.w250225s1.sample1

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.filter.OncePerRequestFilter

@RestController
internal class TestController {
    @PostMapping("/testing/a")
    @PreAuthorize("hasRole('ROLE_ROBOT')")
    fun testingA() {

    }
}

data class YyyAuthentication(
    val value: String,
): Authentication {
    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO("Not yet implemented")
    }

    override fun getCredentials(): Any = value

    override fun getDetails(): Any {
        TODO("Not yet implemented")
    }

    override fun getPrincipal(): Any {
        TODO("Not yet implemented")
    }

    override fun isAuthenticated(): Boolean = true

    override fun setAuthenticated(isAuthenticated: Boolean) = TODO("Not yet implemented")

}

internal class YyyAuthFilter(private val authenticationManager: AuthenticationManager) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationValue = request.getHeader("Authorization")
        println("YyyAuthFilter: $authorizationValue")

        val token = UsernamePasswordAuthenticationToken("", authorizationValue)
        val authResult = authenticationManager.authenticate(token)
        SecurityContextHolder.getContext().authentication = authResult

//        val context: SecurityContext = this.securityContextHolderStrategy.createEmptyContext()
//        context.authentication = authResult
//        this.securityContextHolderStrategy.setContext(context)

        filterChain.doFilter(request, response)
    }

}

@TestConfiguration
@EnableMethodSecurity
internal class TestConfig {

    @Bean
    fun authenticationManager(): AuthenticationManager {
//        val user: UserDetails = User.builder()
//            .username("user1")
//            .password("{noop}pass1") // {noop} oznacza brak enkodowania hasła
//            .roles("ADMIN")
//            .build()

        val p = object : AuthenticationProvider {
            override fun authenticate(authentication: Authentication?): Authentication {
                return UsernamePasswordAuthenticationToken(
                    "robot", "", listOf(SimpleGrantedAuthority("ROLE_ROBOT"))
                )
//                return authentication!!
            }

            override fun supports(authentication: Class<*>?): Boolean {
                return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
            }

        }

        return ProviderManager(p)
    }

    @Bean
    fun yyyAuthFilter(authenticationManager: AuthenticationManager): YyyAuthFilter = YyyAuthFilter(authenticationManager)

    @Bean
    fun securityFilterChain(http: HttpSecurity, yyyAuthFilter: YyyAuthFilter): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
//                it.requestMatchers("/auth/**").permitAll() // endpointy logowania/rejestracji publiczne
                it.anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(
                yyyAuthFilter,
                UsernamePasswordAuthenticationFilter::class.java,
//                    AuthorizationFilter::class.java,
            )
        return http.build()
    }
}

//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@WebMvcTest(
    controllers = [TestController::class],
//    excludeFilters = [@ComponentScan.Filter JwtTokenUtil2::class],
//    excludeAutoConfiguration = [ImportAutoConfiguration::class],
)
@Import(
    TestConfig::class,
)
internal class JwtAuthenticationFilterIntTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun test1() {
        mockMvc.perform(
            post("/testing/a")
//                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "YYY 000111222")
        )
            .andExpect(status().isForbidden)
    }
}