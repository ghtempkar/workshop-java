package my.w250223s3.jwt

import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class Test1 {

    @Test
    fun fun1() {

        val token = JwtTokenUtil2().generateToken(
            User("myadmin", "adminpass", listOf(SimpleGrantedAuthority("ROLE_ADMIN"))),
        )

        val claims = JwtTokenUtil2().getClaimsFromToken(token)

        val ud = userDetailsFromClaims(claims!!)

        println(ud)
    }

}