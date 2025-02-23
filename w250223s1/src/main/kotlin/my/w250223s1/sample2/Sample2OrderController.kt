package my.w250223s1.sample2

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sample2/orders")
class Sample2OrderController(
    private val service: Sample2OrderService,
) {
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createOrder(name: String): String = service.add(name)

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    fun listOrders(): List<String> = service.list()

    @PostMapping("/2")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun createOrder2(@AuthenticationPrincipal user: UserDetails): String {
        println(user)
        return user.authorities.toList().map { it.authority }.sorted().joinToString()
    }

    @PostMapping("/2/json")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    fun createOrder3json(@AuthenticationPrincipal user: UserDetails): List<String> {
        println(user)
        return user.authorities.toList().map { it.authority }.sorted()
    }
}

@Service
class Sample2OrderService() {
    fun add(name: String): String = "add-$name"
    fun list(): List<String> = listOf("1", "2")
}
