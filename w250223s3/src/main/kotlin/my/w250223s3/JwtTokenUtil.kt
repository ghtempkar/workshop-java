package my.w250223s3

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.KeyPair
import java.util.Date

interface JwtTokenUtilInt {
    fun generateToken(userDetails: UserDetails): String

    fun getUsernameFromToken(token: String): String?

    fun validateToken(token: String, userDetails: UserDetails): Boolean
}

@Component
class JwtTokenUtil(
    private val keyPair: KeyPair,
    @Value("\${jwt.expiration}") private val expiration: Long
): JwtTokenUtilInt {
    override fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = mapOf("roles" to userDetails.authorities.map { it.authority })
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(keyPair.private, SignatureAlgorithm.RS256)
            .compact()
    }

    override fun getUsernameFromToken(token: String): String? {
        return getClaimsFromToken(token)?.subject
    }

    override fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    private fun isTokenExpired(token: String): Boolean {
        val expirationDate = getClaimsFromToken(token)?.expiration
        return expirationDate?.before(Date()) ?: true
    }

    private fun getClaimsFromToken(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(keyPair.public)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
    }
}
