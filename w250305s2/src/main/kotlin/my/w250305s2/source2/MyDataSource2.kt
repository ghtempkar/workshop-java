package my.w250305s2.source2

import java.util.Optional
import javax.sql.DataSource
import my.w250305s2.source1.Source1Repo
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Lazy
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
@EnableJdbcRepositories(
//    includeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [Source1Repo::class])],
//    basePackageClasses = [MyEntity1::class],
    basePackages = ["my.w250305s2.source2"],
    jdbcOperationsRef = "dataSource2JdbcOperations",
    dataAccessStrategyRef = "dataSource2DataAccessStrategy",
    transactionManagerRef = "dataSource2TransactionManager",
)
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
//    @Qualifier("dataSource2")
    override fun dataAccessStrategyBean(
        @Qualifier("dataSource2NamedParameterJdbcTemplate") operations: NamedParameterJdbcOperations,
        @Qualifier("dataSource2JdbcConverter") jdbcConverter: JdbcConverter,
        @Qualifier("dataSource2jdbcMappingContext") context: JdbcMappingContext,
        @Qualifier("dataSource2jdbcDialect") dialect: Dialect
    ): DataAccessStrategy {
        return super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect)
    }

    @Bean("dataSource2JdbcConverter")
//    @Qualifier("dataSource2")
    override fun jdbcConverter(
        @Qualifier("dataSource2jdbcMappingContext") mappingContext: JdbcMappingContext,
        @Qualifier("dataSource2NamedParameterJdbcTemplate") operations: NamedParameterJdbcOperations,
        @Qualifier("dataSource2DataAccessStrategy") @Lazy relationResolver: RelationResolver,
        @Qualifier("dataSource2jdbcCustomConversions") conversions: JdbcCustomConversions,
        @Qualifier("dataSource2jdbcDialect") dialect: Dialect
    ): JdbcConverter {
        return super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect)
    }

    @Bean("dataSource2jdbcDialect")
//    @Qualifier("dataSource2")
    override fun jdbcDialect(@Qualifier("dataSource2NamedParameterJdbcTemplate") operations: NamedParameterJdbcOperations): Dialect {
        return super.jdbcDialect(operations)
    }

//    class Ds2NamedParameterJdbcTemplateConfiguration:
//        org.springframework.boot.autoconfigure.jdbc.NamedParameterJdbcTemplateConfiguration {
//    }

    @Bean(name = ["dataSource2NamedParameterJdbcTemplate"])
//    @Qualifier("dataSource2")
    fun dataSource2NamedParameterJdbcTemplate(@Qualifier("dataSource2") sqlServerDs: DataSource): NamedParameterJdbcOperations {
        // todo: check it
        return NamedParameterJdbcTemplate(sqlServerDs)
    }

    @Bean("dataSource2jdbcManagedTypes")
//    @Qualifier("dataSource2")
    override fun jdbcManagedTypes(): RelationalManagedTypes {
        return super.jdbcManagedTypes()
    }

    @Bean("dataSource2jdbcMappingContext")
    @Qualifier("dataSource2")
    override fun jdbcMappingContext(
        namingStrategy: Optional<NamingStrategy>,
        @Qualifier("dataSource2jdbcCustomConversions") customConversions: JdbcCustomConversions,
        @Qualifier("dataSource2jdbcManagedTypes") jdbcManagedTypes: RelationalManagedTypes
    ): JdbcMappingContext {
        return super.jdbcMappingContext(namingStrategy, customConversions, jdbcManagedTypes)
    }

    @Bean("dataSource2jdbcCustomConversions")
//    @Qualifier("dataSource2")
    fun jdbcCustomConversions(@Qualifier("dataSource2jdbcDialect") dialect: Dialect): JdbcCustomConversions {
        try {
//            val dialect = applicationContext.getBean(Dialect::class.java)
            val simpleTypeHolder = if (dialect.simpleTypes().isEmpty())
                JdbcSimpleTypes.HOLDER
            else
                SimpleTypeHolder(dialect.simpleTypes(), JdbcSimpleTypes.HOLDER)

            return JdbcCustomConversions(
                StoreConversions.of(simpleTypeHolder, storeConverters(dialect)), userConverters()
            )
        } catch (exception: NoSuchBeanDefinitionException) {
//            LOG.warn("No dialect found; CustomConversions will be configured without dialect specific conversions")

            return JdbcCustomConversions()
        }
    }
    private fun storeConverters(dialect: Dialect): List<Any> {
        val converters: MutableList<Any> = ArrayList()
        converters.addAll(dialect.converters)
        converters.addAll(JdbcCustomConversions.storeConverters())
        return converters
    }

    @Bean("dataSourc2JdbcAggregateTemplate")
    override fun jdbcAggregateTemplate(
        applicationContext: ApplicationContext,
        @Qualifier("dataSource2jdbcMappingContext") mappingContext: JdbcMappingContext,
        @Qualifier("dataSource2JdbcConverter") converter: JdbcConverter,
        @Qualifier("dataSource2DataAccessStrategy") dataAccessStrategy: DataAccessStrategy
    ): JdbcAggregateTemplate {
        return super.jdbcAggregateTemplate(applicationContext, mappingContext, converter, dataAccessStrategy)
    }



}
