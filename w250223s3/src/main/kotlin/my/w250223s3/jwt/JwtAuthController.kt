package my.w250223s3.jwt

import java.time.Clock
import java.time.Duration
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class JwtAuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenCoder: JwtTokenCoder,
//    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val clock: Clock,
) {

    //    @Value("\${jwt.secret:XXXXXXXXXXXXXXX}")
//    private lateinit var secret: String

//    @Value("\${jwt.expiration:3600000}")
//    private var expiration: Long = 3600000 // 1 godzina domyślnie

    data class AuthRequest(val username: String, val password: String)
    data class AuthResponse(val token: String)

    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthRequest): ResponseEntity<Any> {
        return try {
            val authToken = UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            val authentication = authenticationManager.authenticate(authToken)
            val userDetails = authentication.principal as UserDetails
            val token = jwtTokenCoder.generateToken(userDetails, clock, Duration.ofMinutes(1))
            ResponseEntity.ok(AuthResponse(token))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
        }
    }

//    @PostMapping("/register")
//    fun register(@RequestBody authRequest: AuthRequest): ResponseEntity<Any> {
//        if (userRepository.findByUsername(authRequest.username) != null) {
//            return ResponseEntity.badRequest().body("User already exists")
//        }
//        val user = User(
//            username = authRequest.username,
//            password = passwordEncoder.encode(authRequest.password),
//            roles = "USER" // Nowi użytkownicy otrzymują rolę USER
//        )
//        userRepository.save(user)
//        return ResponseEntity.ok("User registered successfully")
//    }
}

