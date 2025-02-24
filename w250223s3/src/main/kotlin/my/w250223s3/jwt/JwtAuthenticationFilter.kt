package my.w250223s3.jwt

import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.time.Clock
import java.util.Date
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter

interface JwtUserDetailsResolver {
    fun resolve(claims: Claims): UserDetails?

    companion object {
        fun fromService(userDetailsService: UserDetailsService): FromServiceJwtUserDetailsResolver =
            FromServiceJwtUserDetailsResolver(userDetailsService)

        fun fromClaims(): FromClaimsJwtUserDetailsResolver =
            FromClaimsJwtUserDetailsResolver()
    }
}

class FromServiceJwtUserDetailsResolver(
    private val userDetailsService: UserDetailsService,
) : JwtUserDetailsResolver {
    override fun resolve(claims: Claims): UserDetails? {
        return userDetailsFromUserDetailsService(userDetailsService, claims)
    }

    companion object {
        fun userDetailsFromUserDetailsService(userDetailsService: UserDetailsService, claims: Claims): UserDetails? {
            val userDetails = userDetailsService.loadUserByUsername(claims.subject)
            return userDetails
        }
    }
}

class FromClaimsJwtUserDetailsResolver : JwtUserDetailsResolver {
    override fun resolve(claims: Claims): UserDetails? {
        return userDetailsFromClaims(claims)
    }

    companion object {
        fun userDetailsFromClaims(claims: Claims): UserDetails? {
            val authorities = when (val auth = claims["authorities"]) {
                is List<*> -> auth.map { it.toString() } // todo:
                else -> emptyList()
            }

            return User(claims.subject, "", authorities.map { SimpleGrantedAuthority(it) })
        }
    }
}

open class JwtAuthenticationFilter2(
    private val jwtTokenUtil: JwtTokenUtil,
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

        val header = request.getHeader(Authorization)
        if (header == null || !header.startsWith(BearerPrefix)) {
            return
        }

        val token = header.substring(BearerPrefixLen)
        val claims = try {
            jwtTokenUtil.getClaimsFromToken(token)
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
        private const val Authorization: String = "Authorization"
        private const val Bearer: String = "Bearer"
        private const val BearerPrefix: String = Bearer + " "
        private const val BearerPrefixLen = BearerPrefix.length
    }
}
