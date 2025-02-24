package my.w250223s3.jwt

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.time.Clock
import java.time.Duration
import javax.crypto.SecretKey
import my.w250223s3.jwt.FromClaimsJwtUserDetailsResolver.Companion.userDetailsFromClaims
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

class Test1 {

    @Test
    fun fun1() {

        val jwtToken = JwtTokenCoder.ofHS512()
        val clock = Clock.systemDefaultZone()
        val token = jwtToken.generateToken(
            User("myadmin", "adminpass", listOf(SimpleGrantedAuthority("ROLE_ADMIN"))),
            clock,
            Duration.ofMinutes(1),
        )

        val claims = jwtToken.getClaimsFromToken(token)

        val ud = userDetailsFromClaims(claims!!)

        println(ud)
    }

    fun generateRSAKeyPair(): KeyPair {
        // Używamy algorytmu RSA
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        // Ustawiamy długość klucza na 2048 bitów (zalecane minimum)
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.genKeyPair()
    }


    @Test
    fun fun2() {
        val secret2: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512)

        val secret3: KeyPair = generateRSAKeyPair()
        println(secret3)


        val coder1_ = GeneralJwtTokenCoder(
            encryptKey = secret2,
            decryptKey = secret2,
            algorithm = SignatureAlgorithm.HS512,
//            defaultExpirationDelta = Duration.ofMinutes(5),
//            clock = Clock.systemDefaultZone(),
        )
        val coder1 = GeneralJwtTokenCoder(
            encryptKey = secret3.private,
            decryptKey = secret3.public,
            algorithm = SignatureAlgorithm.RS256,
//            defaultExpirationDelta = Duration.ofMinutes(5),
//            clock = Clock.systemDefaultZone(),
        )

        val r1 = coder1.getClaimsFromToken(
            coder1.generateToken(
                User("myuser", "", listOf(SimpleGrantedAuthority("ROLE_A"))),
                Clock.systemDefaultZone(),
                Duration.ofMinutes(5),
            )
        )
        println(r1)
    }

}