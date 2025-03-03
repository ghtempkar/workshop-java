package my.w250303s1

import kotlin.system.measureTimeMillis
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class MyServiceTest(
    @Autowired val myService: MyService,
) {
    @Test
    fun test1() {
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