package my.w250303s1.sample2

import my.w250303s1.sample2.MyTest.IntTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service
import java.time.Instant
import kotlin.system.measureTimeMillis

@Service
internal class MyService {
    @Cacheable("cache-1")
    fun myTimestamp1(): String {
        Thread.sleep(500)
        return Instant.now().toString()
    }

    @Cacheable("cache-2")
    fun myTimestamp2(): String {
        Thread.sleep(500)
        return Instant.now().toString()
    }
}

@Configuration
@EnableCaching
@Import(MyService::class)
internal class MyConfig {
    @Bean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager("cache-1")
    }
}

@SpringBootTest(classes = [IntTestConfig::class])
@Import(
    MyConfig::class
)
internal class MyTest(
    @Autowired val myService: MyService
) {

    @Test
    fun testZero() {
    }

    @Test
    fun initTest(@Autowired cacheManager: CacheManager) {
        assertThat(cacheManager.cacheNames).doesNotContain("some-cache")
        assertThat(cacheManager.cacheNames).contains("cache-1")
    }

    @Test
    fun `test cache-1`() {
        measureTimeMillis {
            println(myService.myTimestamp1())
        }.let {
            println(it)
        }

        measureTimeMillis {
            println(myService.myTimestamp1())
        }.let {
            println(it)
        }
    }

    @Test
    fun `test cache-2 does not exist`() {
        assertThatThrownBy {
            myService.myTimestamp2()
        }.hasMessageContaining("cache-2")
    }

    @Configuration
    internal class IntTestConfig
}
