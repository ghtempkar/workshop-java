package my.w250305s2

import java.util.Optional
import javax.sql.DataSource
import my.w250305s2.source1.MyEntity1
import my.w250305s2.source1.Source1Repo
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Lazy
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jdbc.core.convert.DataAccessStrategy
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.core.convert.RelationResolver
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
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
@EnableJdbcRepositories(
//    includeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [Source1Repo::class])],
    basePackageClasses = [MyEntity1::class],
    jdbcOperationsRef = "dataSource1JdbcOperations",
    dataAccessStrategyRef = "dataSource1DataAccessStrategy",
    transactionManagerRef = "dataSource1TransactionManager",
    )
class MyDataSource1 : AbstractJdbcConfiguration() {

    @Bean
    fun initializeDatabase1(@Qualifier("dataSource1") dataSource: DataSource): ResourceDatabasePopulator {
        val populator = ResourceDatabasePopulator()
        populator.addScript(ClassPathResource("ds1/schema.sql"))
        populator.execute(dataSource)
        return populator
    }

    @Bean
    fun testInit1(@Qualifier("dataSource1") dataSource: DataSource) = CommandLineRunner {
        val template = JdbcTemplate(dataSource)
        val s = template.queryForObject("select current_database()", String::class.java)
        println("---> DS1: $s")
    }

//    @Bean
    fun testInit2(source1Repo: Set<Source1Repo>) = CommandLineRunner {
//        val list1 = source1Repo.findAll()
//        println(list1)
        println(source1Repo)
    }

//    @Bean
    fun testInit2b(source1Repo: Source1Repo) = CommandLineRunner {
        val list1 = source1Repo.findAll()
        println(list1)
    }

    ///////

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

    @Bean("dataSource1TransactionManager")
    fun transactionManager(@Qualifier("dataSource1") dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean("dataSource1DataAccessStrategy")
    @Qualifier("dataSource1")
    override fun dataAccessStrategyBean(
        @Qualifier("dataSource1") operations: NamedParameterJdbcOperations,
        @Qualifier("dataSource1") jdbcConverter: JdbcConverter,
        context: JdbcMappingContext,
        @Qualifier("dataSource1") dialect: Dialect
    ): DataAccessStrategy {
        return super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect)
    }

    @Bean
    @Qualifier("dataSource1")
    override fun jdbcCustomConversions(): JdbcCustomConversions {
        return super.jdbcCustomConversions()
    }

    @Bean
    @Qualifier("dataSource1")
    override fun jdbcManagedTypes(): RelationalManagedTypes {
        return super.jdbcManagedTypes()
    }

    @Bean
    @Qualifier("dataSource1")
    override fun jdbcMappingContext(
        namingStrategy: Optional<NamingStrategy>,
        @Qualifier("dataSource1") customConversions: JdbcCustomConversions,
        @Qualifier("dataSource1") jdbcManagedTypes: RelationalManagedTypes
    ): JdbcMappingContext {
        return super.jdbcMappingContext(namingStrategy, customConversions, jdbcManagedTypes)
    }

    @Bean
    @Qualifier("dataSource1")
    override fun jdbcConverter(
        @Qualifier("dataSource1") mappingContext: JdbcMappingContext,
        @Qualifier("dataSource1") operations: NamedParameterJdbcOperations,
        @Lazy relationResolver: RelationResolver,
        @Qualifier("dataSource1") conversions: JdbcCustomConversions,
        @Qualifier("dataSource1") dialect: Dialect
    ): JdbcConverter {
        return super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect)
    }

    @Bean
    @Qualifier("dataSource1")
    override fun jdbcDialect(@Qualifier("dataSource1") operations: NamedParameterJdbcOperations): Dialect {
        return super.jdbcDialect(operations)
    }

//    class Ds1NamedParameterJdbcTemplateConfiguration:
//        org.springframework.boot.autoconfigure.jdbc.NamedParameterJdbcTemplateConfiguration {
//    }

    @Bean(name = ["dataSource1JdbcOperations"])
    @Qualifier("dataSource1")
    fun ds1JdbcOperations(@Qualifier("dataSource1") sqlServerDs: DataSource): NamedParameterJdbcOperations {
        // todo: check it
        return NamedParameterJdbcTemplate(sqlServerDs)
    }

}

@Configuration
class MyDataSource2 : AbstractJdbcConfiguration() {

    @Bean
    fun initializeDatabase2(@Qualifier("dataSource2") dataSource: DataSource): ResourceDatabasePopulator {
        val populator = ResourceDatabasePopulator()
        populator.addScript(ClassPathResource("ds2/schema.sql"))
        populator.execute(dataSource)
        return populator
    }

    @Bean
    fun ds2testInit2(@Qualifier("dataSource2") dataSource: DataSource) = CommandLineRunner {
        val template = JdbcTemplate(dataSource)
        val s = template.queryForObject("select current_database()", String::class.java)
        println("---> DS2: $s")
    }

    //////////////////

    @Bean
    @Qualifier("dataSource2")
    fun dataSource2(): DataSource {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost:5433/mydatabase2")
            .username("myuser")
            .password("secret")
            .driverClassName("org.postgresql.Driver")
            .build()
    }

    @Bean("dataSource2TransactionManager")
    fun transactionManager(@Qualifier("dataSource2") dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean("dataSource2DataAccessStrategy")
    @Qualifier("dataSource2")
    override fun dataAccessStrategyBean(
        @Qualifier("dataSource2") operations: NamedParameterJdbcOperations,
        @Qualifier("dataSource2") jdbcConverter: JdbcConverter,
        context: JdbcMappingContext,
        @Qualifier("dataSource2") dialect: Dialect
    ): DataAccessStrategy {
        return super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect)
    }

    @Bean("dataSource2JdbcConverter")
    @Qualifier("dataSource2")
    override fun jdbcConverter(
        mappingContext: JdbcMappingContext,
        @Qualifier("dataSource2") operations: NamedParameterJdbcOperations,
        @Lazy relationResolver: RelationResolver,
        conversions: JdbcCustomConversions,
        @Qualifier("dataSource2") dialect: Dialect
    ): JdbcConverter {
        return super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect)
    }

    @Bean("dataSource2jdbcDialect")
    @Qualifier("dataSource2")
    override fun jdbcDialect(@Qualifier("dataSource2") operations: NamedParameterJdbcOperations): Dialect {
        return super.jdbcDialect(operations)
    }

//    class Ds2NamedParameterJdbcTemplateConfiguration:
//        org.springframework.boot.autoconfigure.jdbc.NamedParameterJdbcTemplateConfiguration {
//    }

    @Bean(name = ["dataSource2JdbcOperations"])
    @Qualifier("dataSource2")
    fun ds2JdbcOperations(@Qualifier("dataSource2") sqlServerDs: DataSource): NamedParameterJdbcOperations {
        // todo: check it
        return NamedParameterJdbcTemplate(sqlServerDs)
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
//    MyJdbcConfig::class,
    MyConfig::class,
)
class W250305s2Application

fun main(args: Array<String>) {
    runApplication<W250305s2Application>(*args)
}
