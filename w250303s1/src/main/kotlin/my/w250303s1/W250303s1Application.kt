package my.w250303s1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.time.Instant

@Configuration
class MyConfig {
    @Bean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager("some-cache")
    }
}

@Service
class MyService {
    @Cacheable("some-cache")
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
