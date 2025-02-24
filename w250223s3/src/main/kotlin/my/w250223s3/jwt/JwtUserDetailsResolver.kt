package my.w250223s3.jwt

import io.jsonwebtoken.Claims
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

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