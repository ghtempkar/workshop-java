package my.w250223s1

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController {
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createOrder() {

    }

    //

    @PostMapping("/2")
    @PreAuthorize("hasRole('ADMIN')")
    fun createOrder2(@AuthenticationPrincipal user: UserDetails) {
        println(user)
    }
}