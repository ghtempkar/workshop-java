package my.w250223s3.jwt

import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter

open class JwtAuthenticationFilter(
    private val jwtTokenUtil: JwtTokenUtilInt,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            val username = jwtTokenUtil.getUsernameFromToken(token)
            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsService.loadUserByUsername(username)
                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}

interface JwtUserDetailsResolver {
    fun resolve(claims: Claims): UserDetails?
}

fun userDetailsFromClaims(claims: Claims): UserDetails? {
    val authorities = when(val auth = claims["authorities"]) {
        is List<*> -> auth.map { it.toString() }
        else -> emptyList()
    }
//    val roles = (claims.get("roles") as? List<String>) //?.let { it as String }?.split(",")

    // authorities

    return User(claims.subject, "", authorities.map { SimpleGrantedAuthority(it) })
}

fun userDetailsFromUserDetailsService(userDetailsService: UserDetailsService, claims: Claims): UserDetails? {
    val username = claims.subject

    val userDetails = userDetailsService.loadUserByUsername(username)

    return userDetails
}

open class JwtAuthenticationFilter2(
    private val jwtTokenUtil: JwtTokenUtilInt,
    private val jwtUserDetailsResolver: JwtUserDetailsResolver,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader(Authorization)
        if (header != null && header.startsWith(BearerPrefix)) {
            val token = header.substring(BearerPrefixLen)
//            val username = jwtTokenUtil.getUsernameFromToken(token)

            val claims = jwtTokenUtil.getClaimsFromToken(token)
            if (claims != null && SecurityContextHolder.getContext().authentication == null) {
//                val subject = claim.subject
//                val claimToUserDetails: (Claims) -> UserDetails?

                val userDetails = jwtUserDetailsResolver.resolve(claims)
                if (userDetails != null) {
                    val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
//                    val auth = JwtAuthenticationToken(userDetails, null, userDetails.authorities)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }

//            if (username != null && SecurityContextHolder.getContext().authentication == null) {
//                val userDetails = userDetailsService.loadUserByUsername(username)
//                if (jwtTokenUtil.validateToken(token, userDetails)) {
//                    val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
//                    SecurityContextHolder.getContext().authentication = auth
//                }
//            }
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private const val Authorization: String = "Authorization"
        private const val Bearer: String = "Bearer"
        private const val BearerPrefix: String = Bearer + " "
        private const val BearerPrefixLen = BearerPrefix.length
    }
}
