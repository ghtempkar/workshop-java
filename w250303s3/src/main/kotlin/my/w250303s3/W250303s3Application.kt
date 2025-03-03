package my.w250303s3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MyController {
    @GetMapping("/")
    fun index(): String {
        return "index"
    }
}

@SpringBootApplication
class W250303s3Application

fun main(args: Array<String>) {
    runApplication<W250303s3Application>(*args)
}
