package my.w250305s1

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Table(name = "tw__Towar")
data class MyEntity(
    @Id
    @Column("tw_id")
    val id: Int,

    @Column("tw_symbol")
    val symbol: String?,
)

@Repository
interface MyRepo : CrudRepository<MyEntity, Int>

@Service
class MyService(
    private val myRepo: MyRepo,
) {
    fun run1() {
        try {
            val list1 = myRepo.findAll()
            println(list1)
        } catch (ex: Exception) {
            throw ex
        } finally {
            println()
        }
    }
}

@Configuration
class MyConfig {
    @Bean
    fun init1(@Autowired myService: MyService) = CommandLineRunner {
        myService.run1()
    }
}

@SpringBootApplication
class W250305s1Application

fun main(args: Array<String>) {
    runApplication<W250305s1Application>(*args)
}
