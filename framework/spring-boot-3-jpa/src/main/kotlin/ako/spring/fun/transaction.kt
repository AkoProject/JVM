package ako.spring.`fun`

import ako.spring.SpringRuntime
import org.springframework.transaction.support.DefaultTransactionDefinition

/** 开启事务
 * 在事务块中由于是 inline fun，使用 return 会直接越过函数后续内容直接返回。
 * 所以请注意使用 return 时避免直接 return 到顶层，应使用 return@transaction 来避免直接中断。
 */
inline fun <R> transaction(crossinline block: () -> R): R {
    val transaction = SpringRuntime.EM.instance.platformTransactionManager.getTransaction(
        DefaultTransactionDefinition()
            .apply { propagationBehavior = DefaultTransactionDefinition.PROPAGATION_REQUIRED }
    )

    try {
        return block().apply { SpringRuntime.EM.instance.platformTransactionManager.commit(transaction) }
    } catch (e: Exception) {
        SpringRuntime.EM.instance.platformTransactionManager.rollback(transaction)
        throw e
    }
}