package my.w250303s2.sample1

import com.github.benmanes.caffeine.cache.Caffeine
import my.w250303s2.sample1.MyTest.IntTestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@Service
internal class MyService {

    @Cacheable("my-cache", key = "'value-' + #id", unless = "#result == null")
    fun getValue(id: Int?): String? {
        Thread.sleep(1_000)

        if (id == null) {
            return null
        }

        return "my-value-$id"
    }
}

@Configuration
@EnableCaching
@Import(
    MyService::class,
)
internal class MyTestConfig {
    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager("my-cache")
        cacheManager.setCaffeine(
            Caffeine.newBuilder().expireAfterWrite(1L, TimeUnit.SECONDS)
        )

        return cacheManager
    }

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}

@SpringBootTest(classes = [IntTestConfig::class])
@Import(MyTestConfig::class)
internal class MyTest {

    @Test
    fun firstTest() {
    }

    @Test
    fun testA(@Autowired myService: MyService) {
        measureTimeMillis {
            myService.getValue(10)
        }.let { println(it) }

        measureTimeMillis {
            myService.getValue(10)
        }.let { println(it) }

        Thread.sleep(2_000)

        measureTimeMillis {
            myService.getValue(10)
        }.let { println(it) }
    }

    @Configuration
    internal class IntTestConfig
}
