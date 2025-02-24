package my.w250223s3.jwt

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
    private val jwtTokenUtil: JwtTokenUtil,
//    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    data class AuthRequest(val username: String, val password: String)
    data class AuthResponse(val token: String)

    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthRequest): ResponseEntity<Any> {
        return try {
            val authToken = UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            val authentication = authenticationManager.authenticate(authToken)
            val userDetails = authentication.principal as UserDetails
            val token = jwtTokenUtil.generateToken(userDetails)
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

