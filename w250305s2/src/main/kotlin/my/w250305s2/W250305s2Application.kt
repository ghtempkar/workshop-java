package my.w250305s2

import java.util.Optional
import javax.sql.DataSource
import my.w250305s2.source1.MyDataSource1
import my.w250305s2.source1.MyEntity1
import my.w250305s2.source1.MyJdbcRepositoriesRegistrar
import my.w250305s2.source1.Source1Repo
import my.w250305s2.source2.MyDataSource2
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource
import org.springframework.data.convert.CustomConversions.StoreConversions
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.convert.DataAccessStrategy
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.core.convert.RelationResolver
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext
import org.springframework.data.jdbc.core.mapping.JdbcSimpleTypes
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.relational.RelationalManagedTypes
import org.springframework.data.relational.core.dialect.Dialect
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.transaction.PlatformTransactionManager



@Configuration
@Import(
    MyDataSource1::class,
    MyDataSource2::class,
)
class MyConfig {

}

@SpringBootApplication(exclude = [
    DataSourceAutoConfiguration::class,
    JdbcRepositoriesAutoConfiguration::class,
])
@Import(

//    MyJdbcConfig::class,
    MyConfig::class,
)
class W250305s2Application

fun main(args: Array<String>) {
    runApplication<W250305s2Application>(*args)
}
