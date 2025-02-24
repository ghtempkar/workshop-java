package my.w250223s3.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.time.Clock
import java.util.Date
import javax.crypto.SecretKey
import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails

class JwtTokenUtilHS512(
    private val secret2: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512),
    private val defaultExpirationDelta: Duration = Duration.ofMinutes(5),
    private val clock: Clock,
) : JwtTokenUtil {
//    @Value("\${jwt.secret:XXXXXXXXXXXXXXX}")
//    private lateinit var secret: String

    @Value("\${jwt.expiration:3600000}")
    private var expiration: Long = 3600000 // 1 godzina domyślnie


    override fun generateToken(userDetails: UserDetails): String {
        return generateToken(userDetails, clock, defaultExpirationDelta)
    }

    private fun generateToken(userDetails: UserDetails, clock: Clock, expirationDelta: Duration): String {
        val now = clock.instant()
        val claims: Map<String, Any> = mapOf("authorities" to userDetails.authorities.map { it.authority })
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(expirationDelta)))
            .signWith(secret2)
//            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    override fun getClaimsFromToken(token: String): Claims {
//        return try {
            return Jwts.parserBuilder()
                // todo: requireAudience
//                .requireAudience("string")
                .setSigningKey(secret2)
                .build()
                .parseClaimsJws(token)
                .body
//        } catch (e: Exception) {
//            null
//        }
    }

    companion object {
//        private val secret2 = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}