package ako.rain

import ako.AkoRuntime
import ako.AkoService
import ako.access.AkoAccess
import ako.access.SoftDeleteAccess
import ako.annotation.NoAkoModel
import ako.`fun`.dbModel
import ako.model.base.ModelContext
import ako.protocol.type.AkoTypeProvider
import ako.rain.model.AkoRainModelContext
import org.hibernate.Session
import rain.api.di.DiContext
import rain.api.loader.ApplicationService
import rain.application.loader.ClassRegister
import rain.function.hasAnnotation
import smartaccess.SmartAccess
import smartaccess.jpa.JPAService

class AkoRain(
    val context: DiContext,
    sa: SmartAccess
) : ApplicationService, ClassRegister, AkoRuntime {

    companion object {
        lateinit var instance: AkoRain
            private set
    }

    init {
        AkoService.initRuntime(this)
        instance = this
    }

    val db by lazy {
        (sa.defaultService as? JPAService)?.context ?: error("无法定位 JPAService 的实例！")
    }

    val accessClass = ArrayList<Class<out AkoAccess<*,*>>>()

    override fun register(clazz: Class<*>) {
        if (clazz == AkoAccess::class.java) return
        if (clazz == SoftDeleteAccess::class.java) return
        if (clazz.isInterface && AkoAccess::class.java.isAssignableFrom(clazz))
            accessClass.add(clazz as Class<out AkoAccess<*,*>>)
    }

    val accesses by lazy {
        accessClass.map { context.getBean(it) ?: error("无法定位 Access: ${it.name} 的实例！") }
    }
    val models by lazy {
        accesses.map { it.modelType }
    }

    private val _modelMap by lazy {
        models.filter { !it.hasAnnotation<NoAkoModel>() }.map { it.dbModel }
            .associate { model ->
                model.id to AkoRainModelContext.create(
                    model.id,
                    model,
                    accesses.first { it.modelType == model.type }
                )
            }
    }

    override val modelMap: Map<String, ModelContext<*>>
        get() = _modelMap

    override fun <T : AkoTypeProvider<*, *, *>> getTypeProvider(providerType: Class<T>): T? =
        context.getBean(providerType)

    inline fun <R> transaction(crossinline block: () -> R): R {
        val em = db.getEntityManager("default")
        runCatching { em.unwrap(Session::class.java) }.getOrNull()?.enableFilter("_ako_soft_delete")

        val transaction = em.transaction
        if (transaction.isActive) return block()
        transaction.begin()
        try {
            return block().apply { transaction.commit() }
        } catch (e: Exception) {
            transaction.rollback()
            throw e
        } finally {
            em.close()
        }
    }


}