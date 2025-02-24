package my.w250223s3.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import java.security.KeyPair
import java.time.Clock
import java.util.Date

interface JwtTokenUtil {
    fun generateToken(userDetails: UserDetails): String

    fun getClaimsFromToken(token: String): Claims

    fun getClaimsFromTokenOrNull(token: String): Claims? =
        try {
            getClaimsFromToken(token)
        } catch (ex: Exception) {
            null
        }
}

//@Component
class JwtTokenUtilRS256(
    private val keyPair: KeyPair,
    @Value("\${jwt.expiration}") private val expiration: Long,
    private val clock: Clock,
): JwtTokenUtil {
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

    override fun getClaimsFromToken(token: String): Claims {
//        return try {
           return Jwts.parserBuilder()
                .setSigningKey(keyPair.public)
                .build()
                .parseClaimsJws(token)
                .body
//        } catch (e: Exception) {
//            null
//        }
    }
}
