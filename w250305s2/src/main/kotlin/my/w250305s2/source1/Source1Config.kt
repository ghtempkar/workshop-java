package my.w250305s2.source1

//import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesRegistrar.EnableJdbcRepositoriesConfiguration
import java.util.Optional
import javax.sql.DataSource
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.relational.RelationalManagedTypes
import org.springframework.data.relational.core.dialect.Dialect
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport
import org.springframework.data.repository.config.RepositoryConfigurationExtension
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport
import org.springframework.data.repository.config.RepositoryConfigurationSource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.util.StringUtils

class MyJdbcRepositoryConfigExtension : RepositoryConfigurationExtensionSupport() {
    override fun getModuleName(): String {
        return "JDBC"
    }

    override fun getRepositoryFactoryBeanClassName(): String {
        return JdbcRepositoryFactoryBean::class.java.name
    }

    override fun getModulePrefix(): String {
        return this.moduleName.lowercase()
    }

    override fun getModuleIdentifier(): String {
        return this.modulePrefix
    }

    override fun postProcess(builder: BeanDefinitionBuilder, source: RepositoryConfigurationSource) {
        source.getAttribute("jdbcOperationsRef").filter { str: String? ->
            StringUtils.hasText(
                str
            )
        }.ifPresent { s: String? ->
            builder.addPropertyReference(
                "jdbcOperations",
                s!!
            )
        }
        source.getAttribute("dataAccessStrategyRef").filter { str: String? ->
            StringUtils.hasText(
                str
            )
        }.ifPresent { s: String? ->
            builder.addPropertyReference(
                "dataAccessStrategy",
                s!!
            )
        }
        val transactionManagerRef = source.getAttribute("transactionManagerRef")
        builder.addPropertyValue("transactionManager", transactionManagerRef.orElse("transactionManager"))

//        builder.addPropertyValue("mappingContext", RuntimeBeanReference(JdbcMappingContext::class.java))
        builder.addPropertyReference("mappingContext", "dataSource1JdbcMappingContext")

//        builder.addPropertyValue("dialect", RuntimeBeanReference(Dialect::class.java))
        builder.addPropertyReference("dialect", "dataSource1jdbcDialect")

//        builder.addPropertyValue("converter", RuntimeBeanReference(JdbcConverter::class.java))
        builder.addPropertyReference("converter", "dataSource1JdbcConverter")
    }

    override fun getIdentifyingAnnotations(): Collection<Class<out Annotation?>> {
        return setOf<Class<out Annotation?>>(Table::class.java)
    }

    companion object {
        private const val DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager"
    }
}

@Component
internal class MyJdbcRepositoriesRegistrar : RepositoryBeanDefinitionRegistrarSupport() {
    override fun getAnnotation(): Class<out Annotation?> {
        return EnableJdbcRepositories::class.java
    }

//    override fun getConfiguration(): Class<*> {
//        return EnableJdbcRepositoriesConfiguration::class.java
//    }
    override fun getExtension(): RepositoryConfigurationExtension {
        return MyJdbcRepositoryConfigExtension()
    }
}

// JdbcRepositoryFactory


//@Component
class MyBeanDefinitionModifier : BeanFactoryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        // Pobranie definicji beana
        if (beanFactory.containsBeanDefinition("source1Repo")) {
            val beanDefinition = beanFactory.getBeanDefinition("source1Repo")
            beanDefinition.propertyValues.addPropertyValue("mappingContext", RuntimeBeanReference("dataSource1JdbcMappingContext"))
            println()
            // Zmiana klasy beana
//            beanDefinition.beanClassName = CustomMyService::class.java.getName()
        }
    }
}


@Configuration
@EnableJdbcRepositories(
//    includeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [Source1Repo::class])],
//    basePackageClasses = [MyEntity1::class],
//    basePackageClasses = [Source1Repo::class],
    jdbcOperationsRef = "dataSource1JdbcOperations",
    dataAccessStrategyRef = "dataSource1DataAccessStrategy",
    transactionManagerRef = "dataSource1TransactionManager",
    repositoryFactoryBeanClass = JdbcRepositoryFactoryBean::class,

    )
@Import(
//    MyJdbcRepositoriesRegistrar::class,
)
class MyDataSource1 : AbstractJdbcConfiguration() {

    @Bean
    fun xMyBeanDefinitionModifier(): MyBeanDefinitionModifier = MyBeanDefinitionModifier()

//    @Bean
//    fun source1Repo(f: JdbcRepositoryFactory): Source1Repo {
//        return f.getRepository(Source1Repo::class.java)
//    }

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

        @Bean
    fun testInit2(source1Repo: Set<Source1Repo>) = CommandLineRunner {
//        val list1 = source1Repo.findAll()
//        println(list1)
        println(source1Repo)
    }

        @Bean
    fun testInit2b(source1Repo: Source1Repo) = CommandLineRunner {
        val list1 = source1Repo.findAll()
        println("out1 ---> $list1")
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

    @Bean
    @Qualifier("dataSource1")
    fun jdbcTemplate(@Qualifier("dataSource1") dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }

    @Bean("dataSource1TransactionManager")
    fun transactionManager(@Qualifier("dataSource1") dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Primary
    @Bean("dataSource1jdbcDialect")
//    @Qualifier("dataSource1jdbcDialect")
    override fun jdbcDialect(@Qualifier("dataSource1JdbcOperations") operations: NamedParameterJdbcOperations): Dialect {
        return super.jdbcDialect(operations)
    }

    @Bean("dataSource1DataAccessStrategy")
//    @Qualifier("dataSource1")
    override fun dataAccessStrategyBean(
        @Qualifier("dataSource1JdbcOperations") operations: NamedParameterJdbcOperations,
        @Qualifier("dataSource1JdbcConverter") jdbcConverter: JdbcConverter,
        @Qualifier("dataSource1JdbcMappingContext") context: JdbcMappingContext,
        @Qualifier("dataSource1jdbcDialect") dialect: Dialect
    ): DataAccessStrategy {
        return super.dataAccessStrategyBean(operations, jdbcConverter, context, dialect)
    }

    //    @Bean
//    @Qualifier("dataSource1")
    override fun jdbcCustomConversions(): JdbcCustomConversions {
//        val applicationContext2: ApplicationContext = object ApplicationContext() by applicationContext {
//
//        }
//        applicationContext.
        return super.jdbcCustomConversions()
    }

    @Bean("dataSource1jdbcCustomConversions")
//    @Qualifier("dataSource1")
    fun jdbcCustomConversions(@Qualifier("dataSource1jdbcDialect") dialect: Dialect): JdbcCustomConversions {
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

    @Bean("dataSource1jdbcManagedTypes")
//    @Qualifier("dataSource1")
    override fun jdbcManagedTypes(): RelationalManagedTypes {
        return super.jdbcManagedTypes()
    }



//    @Primary
    @Bean("dataSource1JdbcMappingContext")
//    @Qualifier("dataSource1")
    override fun jdbcMappingContext(
        namingStrategy: Optional<NamingStrategy>,
        @Qualifier("dataSource1jdbcCustomConversions") customConversions: JdbcCustomConversions,
        @Qualifier("dataSource1jdbcManagedTypes") jdbcManagedTypes: RelationalManagedTypes
    ): JdbcMappingContext {
        return super.jdbcMappingContext(namingStrategy, customConversions, jdbcManagedTypes)
    }

    @Primary
    @Bean("dataSource1JdbcConverter")
//    @Qualifier("dataSource1")
    override fun jdbcConverter(
        @Qualifier("dataSource1JdbcMappingContext") mappingContext: JdbcMappingContext,
        @Qualifier("dataSource1JdbcOperations") operations: NamedParameterJdbcOperations,
        @Qualifier("dataSource1DataAccessStrategy") @Lazy relationResolver: RelationResolver,
        @Qualifier("dataSource1jdbcCustomConversions") conversions: JdbcCustomConversions,
        @Qualifier("dataSource1jdbcDialect") dialect: Dialect
    ): JdbcConverter {
        return super.jdbcConverter(mappingContext, operations, relationResolver, conversions, dialect)
    }

    @Bean("dataSourc1JdbcAggregateTemplate")
    override fun jdbcAggregateTemplate(
        applicationContext: ApplicationContext,
        @Qualifier("dataSource1JdbcMappingContext") mappingContext: JdbcMappingContext,
        @Qualifier("dataSource1JdbcConverter") converter: JdbcConverter,
        @Qualifier("dataSource1DataAccessStrategy") dataAccessStrategy: DataAccessStrategy
    ): JdbcAggregateTemplate {
        return super.jdbcAggregateTemplate(applicationContext, mappingContext, converter, dataAccessStrategy)
    }

//    class Ds1NamedParameterJdbcTemplateConfiguration:
//        org.springframework.boot.autoconfigure.jdbc.NamedParameterJdbcTemplateConfiguration {
//    }

    @Bean(name = ["dataSource1JdbcOperations"])
//    @Qualifier("dataSource1")
    fun ds1JdbcOperations(@Qualifier("dataSource1") sqlServerDs: DataSource): NamedParameterJdbcOperations {
        // todo: check it
        return NamedParameterJdbcTemplate(sqlServerDs)
    }

}
