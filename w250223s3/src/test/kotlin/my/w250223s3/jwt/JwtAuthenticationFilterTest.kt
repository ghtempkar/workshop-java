package my.w250223s3.jwt
import io.jsonwebtoken.Claims
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class TestingJwtAuthenticationFilter(
     jwtTokenCoder: JwtTokenCoder, jwtUserDetailsResolver: JwtUserDetailsResolver, clock: Clock,
) : JwtAuthenticationFilter(jwtTokenCoder, jwtUserDetailsResolver, clock) {
    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) = super.doFilterInternal(request, response, filterChain)
}

class JwtAuthenticationFilterTest {

    // Mocks for filter dependencies
    private val jwtTokenCoder: JwtTokenCoder = mockk()
    private val jwtUserDetailsResolver: JwtUserDetailsResolver = mockk()
    // Fixed clock for testing
    private val clock: Clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneId.of("UTC"))
    private lateinit var filter: TestingJwtAuthenticationFilter

    // Mocks for servlet objects
    private val request: HttpServletRequest = mockk(relaxed = true)
    private val response: HttpServletResponse = mockk(relaxed = true)
    private val filterChain: FilterChain = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        // Clear SecurityContext before each test
        SecurityContextHolder.clearContext()
        filter = TestingJwtAuthenticationFilter(jwtTokenCoder, jwtUserDetailsResolver, clock)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `does not initialize when authentication already exists`() {
        // Setup: Set an existing authentication
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken("user", null, emptyList())

        // Execute filter
        filter.doFilterInternal(request, response, filterChain)

        // Verify: filterChain.doFilter is called and authentication remains unchanged
        verify { filterChain.doFilter(request, response) }
        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth)
    }

    @Test
    fun `does not initialize when Authorization header is missing`() {
        every { request.getHeader("Authorization") } returns null

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        // No authentication is set
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `does not initialize when Authorization header does not start with Bearer`() {
        every { request.getHeader("Authorization") } returns "Basic abcdef"

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `does not initialize when exception occurs during token parsing`() {
        val token = "invalidToken"
        every { request.getHeader("Authorization") } returns "Bearer $token"
        every { jwtTokenCoder.getClaimsFromToken(token) } throws RuntimeException("Parsing error")

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `does not initialize when token is expired`() {
        val token = "expiredToken"
        every { request.getHeader("Authorization") } returns "Bearer $token"

        // Mocking Claims with an expiration date before the current time
        val claims: Claims = mockk()
        every { claims.expiration } returns Date.from(clock.instant().minusSeconds(60))
        every { jwtTokenCoder.getClaimsFromToken(token) } returns claims

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `does not initialize when user details cannot be resolved`() {
        val token = "validToken"
        every { request.getHeader("Authorization") } returns "Bearer $token"

        val claims: Claims = mockk()
        every { claims.subject } returns "user1"

        // Set expiration date in the future
        every { claims.expiration } returns Date.from(clock.instant().plusSeconds(3600))
        every { jwtTokenCoder.getClaimsFromToken(token) } returns claims

        // Simulate the case when resolver returns null for user details
        every { jwtUserDetailsResolver.resolve(claims) } returns null

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `sets authentication when token is valid and user details are resolved`() {
        val token = "validToken"
        every { request.getHeader("Authorization") } returns "Bearer $token"

        val claims: Claims = mockk()
        every { claims.expiration } returns Date.from(clock.instant().plusSeconds(3600))
        every { jwtTokenCoder.getClaimsFromToken(token) } returns claims

        // Create sample user details
        val userDetails: UserDetails = mockk(relaxed = true)
        every { userDetails.authorities } returns listOf()
        every { jwtUserDetailsResolver.resolve(claims) } returns userDetails

        filter.doFilterInternal(request, response, filterChain)

        verify { filterChain.doFilter(request, response) }
        val authentication = SecurityContextHolder.getContext().authentication
        assertNotNull(authentication)
        if (authentication is UsernamePasswordAuthenticationToken) {
            assert(authentication.principal == userDetails)
        }
    }
}
