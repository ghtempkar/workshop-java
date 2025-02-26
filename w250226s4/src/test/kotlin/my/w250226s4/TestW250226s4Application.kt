package my.w250226s4

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<W250226s4Application>().with(TestcontainersConfiguration::class).run(*args)
}
