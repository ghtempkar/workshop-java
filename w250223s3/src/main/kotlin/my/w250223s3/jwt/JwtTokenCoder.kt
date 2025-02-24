package my.w250223s3.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.security.KeyPair
import org.springframework.security.core.userdetails.UserDetails
import java.time.Clock
import java.time.Duration
import java.util.Date

interface JwtTokenCoder {
    fun generateToken(userDetails: UserDetails, clock: Clock, expirationDelta: Duration): String

    fun getClaimsFromToken(token: String): Claims

    //    fun getClaimsFromTokenOrNull(token: String): Claims? =
//        try {
//            getClaimsFromToken(token)
//        } catch (ex: Exception) {
//            null
//        }
    companion object {
        fun generateKey(algorithm: SignatureAlgorithm): Key = Keys.secretKeyFor(algorithm)

        fun ofHS512(key: Key = generateKey(SignatureAlgorithm.HS512)): JwtTokenCoder =
            GeneralJwtTokenCoder(key, key, SignatureAlgorithm.HS512)

        fun ofRS256(keyPair: KeyPair): JwtTokenCoder =
            GeneralJwtTokenCoder(keyPair.private, keyPair.public, SignatureAlgorithm.RS256)
    }
}

class GeneralJwtTokenCoder(
    private val encryptKey: Key,
    private val decryptKey: Key,
    private val algorithm: SignatureAlgorithm,
) : JwtTokenCoder {

    override fun generateToken(userDetails: UserDetails, clock: Clock, expirationDelta: Duration): String {
        val now = clock.instant()
        val claims: Map<String, Any> = mapOf("authorities" to userDetails.authorities.map { it.authority })
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(expirationDelta)))
            .signWith(encryptKey, algorithm)
            .compact()
    }

    override fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            // todo: requireAudience
//                .requireAudience("string")
            .setSigningKey(decryptKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

}