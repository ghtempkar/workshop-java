package my.w250224s1

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/foo")
class FooController {

    @SecurityRequirement(name = "basicAuth") // Wymagane uwierzytelnienie w dokumentacji
    @PostMapping
    fun create() {

    }

    @DeleteMapping
    fun delete() {

    }
}