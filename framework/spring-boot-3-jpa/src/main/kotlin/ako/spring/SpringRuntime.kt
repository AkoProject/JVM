package ako.spring

import ako.AkoRuntime
import ako.AkoService
import ako.access.AkoAccess
import ako.model.ModelContext
import ako.model.db.AkoModel
import ako.model.resp.PageResp
import jakarta.persistence.EntityManager
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Import
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Repository
import org.springframework.transaction.PlatformTransactionManager
import java.lang.reflect.ParameterizedType

@AutoConfiguration
@Import(SpringRuntime.EM::class)
open class SpringRuntime : AkoRuntime, ApplicationListener<ContextRefreshedEvent> {

    init {
        AkoService.initRuntime(this)
        instance = this
    }

    companion object{
        lateinit var instance: SpringRuntime
            private set
    }

    @Repository
    class EM(
        val platformTransactionManager: PlatformTransactionManager,
        val entityManager: EntityManager
    ) {
        init {
            instance = this
        }

        companion object {
            lateinit var instance: EM
        }
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val context = event.applicationContext
        val names = context.beanDefinitionNames

        val beans = names.mapNotNull { context.getType(it) }

        beans.asSequence()
            .filter { it.isInterface }
            .filter { AkoAccess::class.java.isAssignableFrom(it) }
            .filter { it != AkoAccess::class.java }
            .map {
                val generic = it.genericInterfaces[0]
                val model = (generic as ParameterizedType).actualTypeArguments[0] as Class<out AkoModel>

                val access = context.getBeansOfType(it).values.first() as AkoAccess<*>
                model to access
            }
            .let { AkoService.loadModel(it as List<Pair<Class<AkoModel>, AkoAccess<AkoModel>>>) }
    }

    override fun <T : AkoModel> whereList(
        dbModel: ModelContext<T>,
        params: Map<String, Any>?,
        sort: Map<String, String>?,
        pid: Int?,
        pSize: Int?
    ): List<T> =
        ako.`fun`.whereList(dbModel, params, sort, pid, pSize) { query, type ->
            EM.instance.entityManager.createQuery(query, type)
        }

    override fun <T : AkoModel> wherePage(
        dbModel: ModelContext<T>,
        params: Map<String, Any>?,
        sort: Map<String, String>?,
        pid: Int,
        pSize: Int
    ): PageResp<T> =
        ako.`fun`.wherePage(dbModel, params, sort, pid, pSize) { query, type ->
            if (type != null) EM.instance.entityManager.createQuery(query, type)
            else EM.instance.entityManager.createQuery(query)
        }

}