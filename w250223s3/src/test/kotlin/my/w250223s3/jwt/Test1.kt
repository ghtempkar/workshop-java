package my.w250223s3.jwt

import java.time.Clock
import my.w250223s3.jwt.FromClaimsJwtUserDetailsResolver.Companion.userDetailsFromClaims
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class Test1 {

    @Test
    fun fun1() {

        val jwtToken = JwtTokenUtilHS512(clock = Clock.systemDefaultZone())

        val token = jwtToken.generateToken(
            User("myadmin", "adminpass", listOf(SimpleGrantedAuthority("ROLE_ADMIN"))),
        )

        val claims = jwtToken.getClaimsFromToken(token)

        val ud = userDetailsFromClaims(claims!!)

        println(ud)
    }

}