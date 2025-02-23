package my.w250223s1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootApplication
@ComponentScan(
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ["my\\.w250223s1\\.sample1\\..*Config"]
        ),
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ["my\\.w250223s1\\.sample2\\..*Config"]
        ),
    ]
)
class W250223s1Application

fun main(args: Array<String>) {
    runApplication<W250223s1Application>(*args)
}
