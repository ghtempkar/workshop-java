## w250305s1

- jdbc CrudRepository

## w250303s4

- thymeleaf, htmx

## w250303s3

- thymeleaf

## w250303s1

- implementation("org.springframework.boot:spring-boot-starter-cache")
- @EnableCaching @Cacheable
- `@Cacheable("cache-1", key = "'my-timestamp-1'")`
- `@Cacheable("cache-1", key = "'my-order4-' + #id")`
- `@Cacheable("cache-1", key = "'my-status5-' + #myUser.id")`
- `@Cacheable("cache-1", key = "#root.methodName + '-' + #myUser.id")`
- `@Cacheable("cache-1", key = "'text7'", unless = "#result == null")`
- `ConcurrentMapCacheManager()`

## w250303s2

- `implementation("com.github.ben-manes.caffeine:caffeine:3.1.6")`
- `CaffeineCacheManager` , `.expireAfterWrite()` , `.ticker()`
- `MutableClock`

## w250222s1

## w250222s2

## w250222s3

- API_KEY
- @Bean SecurityFilterChain
  - .addFilterBefore()

## w250222s4

## w250223s1

- Basic Auth
- InMemoryUserDetailsManager
- @WebMvcTest
- @WithMockUser
- @MockitoBean

## w250224s2

- MFA

## w250226s1

- jdbc m2
- @JdbcTest , @Sql

## w250226s2

- jdbc postgres
- test containers

## w250226s2

- jdbc postgres
- test containers
- flyway