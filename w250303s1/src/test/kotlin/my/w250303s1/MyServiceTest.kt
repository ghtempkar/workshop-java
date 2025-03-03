package my.w250303s1

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import kotlin.system.measureTimeMillis

@SpringBootTest
internal class MyServiceTest(
    @Autowired val myService: MyService
) {
    @Test
    fun initTest(@Autowired cacheManager: CacheManager) {
        assertThat(cacheManager.cacheNames).contains("some-cache")
    }

    @Test
    fun cache1test(@Autowired cacheManager: CacheManager) {
        measureTimeMillis {
            println(myService.myTimestampNow())
        }.also {
            println(it)
        }
        measureTimeMillis {
            println(myService.myTimestampNow())
        }.also {
            println(it)
        }
    }
}
