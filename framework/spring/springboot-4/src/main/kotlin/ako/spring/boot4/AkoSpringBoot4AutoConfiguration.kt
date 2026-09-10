package ako.spring.boot4

import ako.spring.AkoSpringRuntime
import ako.spring.db.AkoSpringDatabase
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.transaction.PlatformTransactionManager

/** Spring Boot 4 auto-configuration. */
@AutoConfiguration
@ConditionalOnMissingBean(AkoSpringRuntime::class)
open class AkoSpringBoot4AutoConfiguration {

    @Bean
    open fun akoSpringRuntime(
        applicationContext: ApplicationContext,
        databases: List<AkoSpringDatabase>,
        transactionManager: ObjectProvider<PlatformTransactionManager>,
    ): AkoSpringRuntime = AkoSpringRuntime(
        applicationContext,
        databases,
        transactionManager.getIfAvailable(),
    )
}
