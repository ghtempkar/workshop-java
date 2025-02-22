package my.w250222s3

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
class AuthController(private val authenticationManager: AuthenticationManager) {

    data class LoginRequest(val username: String, val password: String)
    data class LoginResponse(val message: String)

    // Endpoint logowania – weryfikacja poświadczeń i utworzenie sesji
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest, request: HttpServletRequest): LoginResponse {
        val token = UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        val authentication = authenticationManager.authenticate(token)
        SecurityContextHolder.getContext().authentication = authentication
        // Utwórz sesję i zapisz w niej kontekst bezpieczeństwa
        val session = request.getSession(true)
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext())
        return LoginResponse("Login successful")
    }

    // Endpoint wylogowywania – unieważnienie sesji
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): LoginResponse {
        request.getSession(false)?.invalidate()
        SecurityContextHolder.clearContext()
        return LoginResponse("Logout successful")
    }
}
