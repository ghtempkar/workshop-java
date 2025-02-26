package my.w250226s2

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<W250226s2Application>().with(TestcontainersConfiguration::class).run(*args)
}
