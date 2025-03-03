package my.w250303s2.sample2

import com.github.benmanes.caffeine.cache.Caffeine
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
import java.time.Instant
import java.time.ZoneId
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

class MutableClock(private var instant: Instant, private val zoneId: ZoneId) : Clock() {
    override fun getZone(): ZoneId = zoneId
    override fun withZone(zone: ZoneId): Clock = MutableClock(instant, zone)
    override fun instant(): Instant = instant

    fun setTime(newInstant: Instant) {
        instant = newInstant
    }

    fun advanceBySeconds(seconds: Long) {
        instant = instant.plusSeconds(seconds)
    }
}

@Configuration
@EnableCaching
@Import(
    MyService::class
)
internal class MyTestConfig {
    @Bean
    fun clock(): MutableClock {
        return MutableClock(Instant.now(), ZoneId.systemDefault())
    }

    @Bean
    fun cacheManager(clock: Clock): CacheManager {
        val cacheManager = CaffeineCacheManager("my-cache")
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(5L, TimeUnit.SECONDS)
                .ticker { clock.millis() * 1_000_000 }
        )

        return cacheManager
    }
}

@SpringBootTest(classes = [MyTest.IntTestConfig::class])
@Import(MyTestConfig::class)
internal class MyTest {

    @Test
    fun firstTest() {
    }

    @Test
    fun `simple test 1`(
        @Autowired myService: MyService,
        @Autowired clock: MutableClock
    ) {
        measureTimeMillis {
            myService.getValue(10)
        }.let { println(it) }

        measureTimeMillis {
            myService.getValue(10)
        }.let { println(it) }

        clock.advanceBySeconds(6)

        measureTimeMillis {
            myService.getValue(10)
        }.let { println(it) }
    }

    @Configuration
    internal class IntTestConfig
}
