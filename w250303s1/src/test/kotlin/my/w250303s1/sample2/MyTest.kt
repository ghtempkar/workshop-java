package my.w250303s1.sample2

import my.w250303s1.sample2.MyTest.IntTestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
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
internal class MyTest {

    @Test
    fun testZero() {
    }

    @Test
    fun initTest(@Autowired cacheManager: CacheManager) {
        assertThat(cacheManager.cacheNames).doesNotContain("some-cache")
        assertThat(cacheManager.cacheNames).contains("cache-1")
    }

    @Configuration
    internal class IntTestConfig
}
