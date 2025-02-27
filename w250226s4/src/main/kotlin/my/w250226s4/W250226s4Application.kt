package my.w250226s4

import java.util.Properties
import javax.sql.DataSource
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject

@SpringBootApplication
class W250226s4Application

@Configuration
class MyConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    fun primaryDatasourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    fun datasource(primaryDatasourceProperties: DataSourceProperties): DataSource =
        primaryDatasourceProperties.initializeDataSourceBuilder().build()

    @Bean
    fun initCheck1(jdbcTemplate: JdbcTemplate) = CommandLineRunner {
        val s = jdbcTemplate.queryForObject<String>("SELECT CURRENT_TIME")
        println(s)
    }
}

fun main(args: Array<String>) {
    runApplication<W250226s4Application>(*args)
}
