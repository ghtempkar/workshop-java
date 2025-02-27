package my.w250227s3

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<W250227s3Application>().with(TestcontainersConfiguration::class).run(*args)
}
