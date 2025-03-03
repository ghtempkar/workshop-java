package my.w250303s1

import java.time.Instant
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.stereotype.Service

@Service
class MyService {
    @Cacheable("my-timestamp-now")
    fun myTimestampNow(): String {
        Thread.sleep(500)
        return Instant.now().toString()
    }
}

@SpringBootApplication
@EnableCaching
class W250303s1Application

fun main(args: Array<String>) {
    runApplication<W250303s1Application>(*args)
}
