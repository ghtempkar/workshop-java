package my.w250305s2

import javax.sql.DataSource
import my.w250305s2.source1.MyEntity1
import my.w250305s2.source1.Source1Repo
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Lazy
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jdbc.core.convert.DataAccessStrategy
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.core.convert.RelationResolver
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.data.relational.core.dialect.Dialect
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionManager

@Configuration
class MyJdbcConfig : org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration() {

    @Bean("dataSource1DataAccessStrategy")
    @Qualifier("dataSource1")
    override fun dataAccessStrategyBean(
        @Qualifier("qualifierJdbcOperations")operations: NamedParameterJdbcOperations,
        @Qualifier("dataSource1") jdbcConverter: JdbcConverter,
        context: JdbcMappingContext,
        @Qualifier("dataSource1")dialect: Dialect
    ): DataAccessStrategy {
        return super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect)
    }

    @Bean
    @Qualifier("dataSource1")
    override fun jdbcConverter(
        mappingContext: JdbcMappingContext,
        @Qualifier("qualifierJdbcOperations") operations: NamedParameterJdbcOperations,
        @Lazy relationResolver: RelationResolver,
        conversions: JdbcCustomConversions,
        @Qualifier("dataSource1") dialect: Dialect
    ): JdbcConverter {
        return super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect)
    }

    @Bean
    @Qualifier("dataSource1")
    override fun jdbcDialect(@Qualifier("qualifierJdbcOperations") operations: NamedParameterJdbcOperations): Dialect {
        return super.jdbcDialect(operations)
    }
}

@Configuration
@EnableJdbcRepositories(
    basePackageClasses = [MyEntity1::class],
//    basePackages = ["my.w250305s2.source1"],
    jdbcOperationsRef = "qualifierJdbcOperations",
    dataAccessStrategyRef = "dataSource1DataAccessStrategy",
    transactionManagerRef = "ds1transactionManager",

)
class MyDataSource1 {

    @Bean
    fun initializeDatabase1(@Qualifier("dataSource1") dataSource: DataSource): ResourceDatabasePopulator {
        val populator = ResourceDatabasePopulator()
        populator.addScript(ClassPathResource("ds1/schema.sql")) // Wczytuje plik schema.sql z katalogu resources
        populator.execute(dataSource) // Wykonuje skrypt na podanym DataSource
        return populator
    }

    @Bean("ds1transactionManager")
    fun ds1transactionManager(@Qualifier("dataSource1") dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    @Qualifier("dataSource1")
    fun namedParameterJdbcTemplate(@Qualifier("dataSource1") dataSource: DataSource): NamedParameterJdbcOperations {
        return NamedParameterJdbcTemplate(dataSource)
    }

    @Bean
    @Qualifier("dataSource1")
    fun dataSource1(): DataSource {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost:5432/mydatabase1")
            .username("myuser")
            .password("secret")
            .driverClassName("org.postgresql.Driver")
            .build()
    }

    @Bean(name = ["qualifierJdbcOperations"])
    @Qualifier("qualifierJdbcOperations")
    fun ds1JdbcOperations(@Qualifier("dataSource1") sqlServerDs: DataSource): NamedParameterJdbcOperations {
        return NamedParameterJdbcTemplate(sqlServerDs)
    }

    @Bean
    fun testInit1(@Qualifier("dataSource1") dataSource: DataSource) = CommandLineRunner {
        val template = JdbcTemplate(dataSource)
        val s = template.queryForObject("select current_database()", String::class.java)
        println("---> $s")
    }

    @Bean
    fun testInit2(source1Repo: Source1Repo) = CommandLineRunner {
        val list1 = source1Repo.findAll()
        println(list1)
    }
}

@Configuration
class MyDataSource2 {
    @Bean
    @Qualifier("dataSource2")
    fun dataSource2(): DataSource {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost:5433/mydatabase2")
            .username("myuser")
            .password("mypassword")
            .driverClassName("org.postgresql.Driver")
            .build()
    }
}

@Configuration
@Import(
    MyDataSource1::class,
    MyDataSource2::class,
)
class MyConfig {

}

@SpringBootApplication
@Import(
    MyJdbcConfig::class,
    MyConfig::class,
)
class W250305s2Application

fun main(args: Array<String>) {
    runApplication<W250305s2Application>(*args)
}
