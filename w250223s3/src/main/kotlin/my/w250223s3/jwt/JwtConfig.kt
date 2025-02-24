package my.w250223s3.jwt

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfig {

    @Bean
    fun jwtTokenCoder(): JwtTokenCoder =
        JwtTokenCoder.ofHS512( JwtTokenCoder.generateKey(SignatureAlgorithm.HS512))

    @Bean
    fun jwtAuthenticationFilter(jwtTokenCoder: JwtTokenCoder, clock: Clock): JwtAuthenticationFilter =
        JwtAuthenticationFilter(jwtTokenCoder, FromClaimsJwtUserDetailsResolver(), clock)

}