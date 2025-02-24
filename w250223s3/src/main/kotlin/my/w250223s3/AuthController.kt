package my.w250223s3

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {

    @PostMapping("/auth/login")
    fun login() {
    }

    @GetMapping("/auth/me")
    fun me() {
    }
}
