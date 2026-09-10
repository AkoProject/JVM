package ako.spring.boot2

import ako.spring.AkoSpringRuntime
import ako.spring.db.AkoSpringDatabase
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/** Boot 2.7 auto-configuration; no Jakarta Persistence dependency is leaked. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(AkoSpringRuntime::class)
open class AkoSpringBoot2AutoConfiguration {

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
