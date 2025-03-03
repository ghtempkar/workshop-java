package my.w250303s1

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
