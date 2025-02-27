package my.w250227s1

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<W250227s1Application>().with(TestcontainersConfiguration::class).run(*args)
}
