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
    @Cacheable("cache-1", key = "'my-timestamp-1'")
    fun myTimestamp1(): String {
        Thread.sleep(500)
        return Instant.now().toString()
    }

    @Cacheable("cache-2")
    fun myTimestamp2(): String {
        Thread.sleep(500)
        return Instant.now().toString()
    }

    @Cacheable("cache-1", key = "'my-value-3'")
    fun myValue3(value: String): String {
        Thread.sleep(500)
        return value
    }

    @Cacheable("cache-1", key = "'my-order4-' + #id")
    fun myOrder4(id: Int): String {
        return "my-order4-value-$id"
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

    @Test
    fun `test cache-1 values`(@Autowired cacheManager: CacheManager) {
        val cache = requireNotNull(cacheManager.getCache("cache-1")).also { it.clear() }

        assertThat(cache.get("my-timestamp-1")).isNull()

        println(measureTimeMillis { println(myService.myTimestamp1()) })

        assertThat(cache.get("my-timestamp-1")).isNotNull()
    }

    @Test
    fun `test cache-1 my-value-3`(@Autowired cacheManager: CacheManager) {
        val cache = requireNotNull(cacheManager.getCache("cache-1")).also { it.clear() }

        assertThat(cache.get("my-value-3")).isNull()

        println(measureTimeMillis { println(myService.myValue3("AAA")) })

        assertThat(cache.get("my-value-3")).isNotNull()
        assertThat(cache.get("my-value-3")?.get()).isEqualTo("AAA")
    }

    @Test
    fun `test cache-1 my-order4`(@Autowired cacheManager: CacheManager) {
        val cache = requireNotNull(cacheManager.getCache("cache-1")).also { it.clear() }

        assertThat(cache.get("my-order4-100")).isNull()
        assertThat(cache.get("my-order4-102")).isNull()

        myService.myOrder4(100)
        myService.myOrder4(102)

        assertThat(cache.get("my-order4-100")?.get()).isEqualTo("my-order4-value-100")
        assertThat(cache.get("my-order4-102")?.get()).isEqualTo("my-order4-value-102")
    }

    @Configuration
    internal class IntTestConfig
}
