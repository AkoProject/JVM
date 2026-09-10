package ako.spring.data

import ako.spring.db.AkoSpringDatabase
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.PlatformTransactionManager

/**
 * Spring Data JPA is optional. When it is present this configuration supplies
 * the database SPI implementation consumed by Ako's Boot 3/4 runtime.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JpaRepository::class)
@ConditionalOnMissingBean(AkoSpringDatabase::class)
open class AkoSpringDataAutoConfiguration {

    @Bean
    open fun akoSpringDataDatabase(
        applicationContext: ApplicationContext,
        entityManager: ObjectProvider<EntityManager>,
        transactionManager: ObjectProvider<PlatformTransactionManager>,
    ): AkoSpringDatabase = AkoSpringDataDatabase(
        applicationContext,
        entityManager.getIfAvailable() ?: error("未找到 JPA EntityManager"),
        transactionManager.getIfAvailable() ?: error("未找到 PlatformTransactionManager")
    )
}
