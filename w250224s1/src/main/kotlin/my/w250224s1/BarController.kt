package my.w250224s1

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bar")
@SecurityRequirement(name = "basicAuth")
class BarController {

    @PostMapping("/import-all")
    fun importAll() {

    }

    @PostMapping("/export-all")
    fun exportAll() {

    }

}