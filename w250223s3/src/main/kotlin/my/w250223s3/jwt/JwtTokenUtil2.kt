package my.w250223s3.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails

class JwtTokenUtil2(
    private val secret2:SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)
): JwtTokenUtilInt {
//    @Value("\${jwt.secret:XXXXXXXXXXXXXXX}")
//    private lateinit var secret: String

    @Value("\${jwt.expiration:3600000}")
    private var expiration: Long = 3600000 // 1 godzina domyślnie



    override fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = mapOf("authorities" to userDetails.authorities.map { it.authority })
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(secret2)
//            .signWith(SignatureAlgorithm.HS512, secret)
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
        val expiration = getClaimsFromToken(token)?.expiration
        return expiration?.before(Date()) ?: true
    }

    override fun getClaimsFromToken(token: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(secret2).parseClaimsJws(token).body
        } catch (e: Exception) {
            null
        }
    }

    companion object {
//        private val secret2 = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}