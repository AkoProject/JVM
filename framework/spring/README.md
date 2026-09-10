# Ako Spring

`ako-spring` 是基于 Spring Boot 实现的 Ako 集成。项目明确区分不同的
Spring Boot 版本，使公共运行时不直接依赖 `javax.persistence` 或
`jakarta.persistence`。

## 模块

- `ako-spring-common` —— Spring 公共运行时、模型上下文、延迟解析的
  `AkoAccess` 代理、事务支持，以及简洁的 `AkoSpringDatabase` 数据库操作接口。
- `ako-springboot-2` —— Spring Boot 2.7 自动配置，Java 8 基线。
- `ako-springboot-3` —— Spring Boot 3 自动配置，Java 17 基线。
- `ako-springboot-4` —— Spring Boot 4 自动配置，Java 17 基线。
- `ako-springboot-springdata` —— 基于 Jakarta Persistence 的 Spring Data JPA
  实现。该模块以 Spring Data JPA 4.x 为编译目标，同时只使用与 Boot 3 所使用的
  Spring Data JPA 3.x 共有的 API。

因此，Spring Data JPA 支持 Spring Boot 3 和 Spring Boot 4。Spring Boot 2
可以使用 `ako-spring-common`，并自行实现 `AkoSpringDatabase`；但不提供
Spring Data Repository 实现的 Boot 2 兼容保证，因为 `ako-jpa` 使用 Jakarta
Persistence，而 Boot 2 的 JPA 技术栈使用 `javax.persistence`。

## Boot 3/4 常规配置

```kotlin
dependencies {
    implementation("com.ako-dev:ako-springboot-3:<ako-version>")
    implementation("com.ako-dev:ako-springboot-springdata:<ako-version>")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
```

Boot 4 只需要将 `ako-springboot-3` 替换为 `ako-springboot-4`。Spring Boot
的依赖管理会提供匹配的 Spring 和 Spring Data 版本。

如果需要在 Kotlin 伴生对象中直接代理 Repository，可以继承 Ako 提供的基础接口：

```kotlin
interface SchoolRepository : AkoJpaRepository<School, Int>

@Entity
class School : CompleteModel() {
    companion object : SchoolRepository by findAccess()
}
```

普通的 `JpaRepository<Model, Id>` Bean 也会被自动发现并用于模型分页；继承
`AkoJpaRepository` 后，还可以通过 `findAccess()` 获取该 Repository。

如果使用其他数据库，只需提供一个 `AkoSpringDatabase` Bean，声明它负责的模型，
并实现列表查询、分页查询、保存和删除操作即可。Ako 的元数据处理和模型生命周期
仍由公共层统一完成。
