package my.w250223s3.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

internal class TestingJwtAuthenticationFilter(jwtTokenUtil: JwtTokenUtil, userDetailsService: UserDetailsService) :
    JwtAuthenticationFilter(jwtTokenUtil, userDetailsService) {
    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) = super.doFilterInternal(request, response, filterChain)
}

class JwtAuthenticationFilterTest {

    private lateinit var jwtTokenUtil: JwtTokenUtil
    private lateinit var userDetailsService: UserDetailsService
    private lateinit var filterChain: FilterChain
    private lateinit var filter: TestingJwtAuthenticationFilter

    @BeforeEach
    fun setUp() {
        jwtTokenUtil = mock(JwtTokenUtil::class.java)
        userDetailsService = mock(UserDetailsService::class.java)
        filterChain = mock(FilterChain::class.java)
        filter = TestingJwtAuthenticationFilter(jwtTokenUtil, userDetailsService)
        SecurityContextHolder.clearContext()
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should continue filter chain when no Authorization header`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        filter.doFilterInternal(request, response, filterChain)

        // Brak ustawionego uwierzytelnienia
        assertNull(SecurityContextHolder.getContext().authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `should continue filter chain when header does not start with Bearer`() {
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Basic abcdef")
        val response = MockHttpServletResponse()

        filter.doFilterInternal(request, response, filterChain)

        // Uwierzytelnienie nie powinno być ustawione
        assertNull(SecurityContextHolder.getContext().authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `should set authentication when valid token provided`() {
        val token = "validToken"
        val username = "testuser"
        val authHeader = "Bearer $token"

        val request = MockHttpServletRequest()
        request.addHeader("Authorization", authHeader)
        val response = MockHttpServletResponse()

        // Konfiguracja mocków
        `when`(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(username)
        val userDetails: UserDetails = User.withUsername(username)
            .password("password")
            .roles("USER")
            .build()
        `when`(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails)
        `when`(jwtTokenUtil.validateToken(token, userDetails)).thenReturn(true)

        filter.doFilterInternal(request, response, filterChain)

        // Uwierzytelnienie powinno zostać ustawione w SecurityContextHolder
        val authentication = SecurityContextHolder.getContext().authentication
        assertNotNull(authentication)
        assertEquals(username, authentication?.name)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `should not set authentication when token validation fails`() {
        val token = "invalidToken"
        val username = "testuser"
        val authHeader = "Bearer $token"

        val request = MockHttpServletRequest()
        request.addHeader("Authorization", authHeader)
        val response = MockHttpServletResponse()

        `when`(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(username)
        val userDetails: UserDetails = User.withUsername(username)
            .password("password")
            .roles("USER")
            .build()
        `when`(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails)
        `when`(jwtTokenUtil.validateToken(token, userDetails)).thenReturn(false)

        filter.doFilterInternal(request, response, filterChain)

        // Uwierzytelnienie nie powinno być ustawione
        assertNull(SecurityContextHolder.getContext().authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `should not set authentication when username is null`() {
        val token = "tokenWithNoUsername"
        val authHeader = "Bearer $token"

        val request = MockHttpServletRequest()
        request.addHeader("Authorization", authHeader)
        val response = MockHttpServletResponse()

        `when`(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(null)

        filter.doFilterInternal(request, response, filterChain)

        // Uwierzytelnienie nie powinno być ustawione
        assertNull(SecurityContextHolder.getContext().authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }
}
