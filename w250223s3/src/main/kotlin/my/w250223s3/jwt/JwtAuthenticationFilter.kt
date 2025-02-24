package my.w250223s3.jwt

import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.time.Clock
import java.util.Date
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

open class JwtAuthenticationFilter(
    private val jwtTokenCoder: JwtTokenCoder,
    private val jwtUserDetailsResolver: JwtUserDetailsResolver,
    private val clock: Clock,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        initializeSecurityContext(request)
        filterChain.doFilter(request, response)
    }

    private fun initializeSecurityContext(request: HttpServletRequest) {
        if (SecurityContextHolder.getContext().authentication != null) {
            return
        }

        val header = request.getHeader(AUTHORIZATION)
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return
        }

        val token = header.substring(BEARER_PREFIX_LEN)
        val claims = try {
            jwtTokenCoder.getClaimsFromToken(token)
        } catch (ex: Exception) {
            logger.info("unable to parse token: ${ex.message}", ex)
            return
        }

        if (isTokenExpired(claims)) {
            logger.info("token expired")
            return
        }

        val userDetails = jwtUserDetailsResolver.resolve(claims)
        if (userDetails == null) {
            logger.info("unable to resolve user: ${claims.subject}")
            return
        }

        val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = auth
    }

    private fun isTokenExpired(claims: Claims): Boolean {
        val expirationDate = claims.expiration
        return expirationDate?.before(Date.from(clock.instant())) ?: true
    }

    companion object {
        private const val AUTHORIZATION: String = "Authorization"
        private const val BEARER: String = "Bearer"
        private const val BEARER_PREFIX: String = "$BEARER "
        private const val BEARER_PREFIX_LEN = BEARER_PREFIX.length
    }
}
