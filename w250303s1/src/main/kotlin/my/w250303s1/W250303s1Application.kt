package my.w250303s1

import java.time.Instant
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service

@SpringBootApplication
class W250303s1Application

@Service
class MyService {
//	@Cache
	fun myFun1(): String {
		Thread.sleep(500)
		return Instant.now().toString()
	}
}

fun main(args: Array<String>) {
	runApplication<W250303s1Application>(*args)
}
