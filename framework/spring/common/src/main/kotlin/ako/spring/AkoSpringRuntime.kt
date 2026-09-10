package ako.spring

import ako.AkoRuntime
import ako.AkoService
import ako.annotation.NoAkoModel
import ako.`fun`.dbModel
import ako.model.base.AkoModel
import ako.model.base.ModelContext
import ako.model.resp.PageResp
import ako.protocol.type.AkoTypeProvider
import ako.spring.access.AkoAccess
import ako.spring.db.AkoSpringDatabase
import ako.spring.model.AkoSpringModelContext
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 * Spring-side runtime shared by all supported Boot generations.
 *
 * It intentionally knows only Spring Framework contracts. Boot 2/3/4
 * modules are limited to auto-configuration glue, while database behavior is
 * supplied by [AkoSpringDatabase].
 */
open class AkoSpringRuntime(
    val applicationContext: ApplicationContext,
    databases: List<AkoSpringDatabase> = emptyList(),
    private val transactionManager: PlatformTransactionManager? = null,
) : AkoRuntime, ApplicationListener<ContextRefreshedEvent> {

    private val databases = databases.toList()

    @Volatile
    private var initializedModelMap: Map<String, ModelContext<*>>? = null

    private val constructedTypeProviders = ConcurrentHashMap<Class<*>, AkoTypeProvider<*, *, *>>()

    init {
        AkoService.initRuntime(this)
        instance = this
    }

    companion object {
        @Volatile
        lateinit var instance: AkoSpringRuntime
            private set
    }

    override val modelMap: Map<String, ModelContext<*>>
        get() {
            initializeModels()
            return initializedModelMap.orEmpty()
        }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        initializeModels()
    }

    override fun <T : AkoTypeProvider<*, *, *>> getTypeProvider(providerType: Class<T>): T? {
        val contextBeans = applicationContext.getBeansOfType(providerType).values.firstOrNull()
        if (contextBeans != null) return contextBeans

        @Suppress("UNCHECKED_CAST")
        return constructedTypeProviders.getOrPut(providerType) {
            runCatching { providerType.getDeclaredConstructor().newInstance() }
                .getOrNull()
                ?: return null
        } as T
    }

    /** Resolve an Ako access bean without forcing it during model discovery. */
    @Suppress("UNCHECKED_CAST")
    fun <T : AkoAccess<*, *>> accessOf(accessType: Class<T>): T {
        runCatching { applicationContext.getBean(accessType) }
            .getOrNull()
            ?.let { return it as T }

        applicationContext.getBeansOfType(AkoAccess::class.java).values
            .firstOrNull { accessType.isInstance(it) }
            ?.let { return it as T }

        error("无法找到 Ako Access: ${accessType.name}")
    }

    fun <R> transaction(block: () -> R): R {
        val manager = transactionManager ?: return block()
        val definition = DefaultTransactionDefinition().apply {
            propagationBehavior = DefaultTransactionDefinition.PROPAGATION_REQUIRED
        }
        val template = TransactionTemplate(manager, definition)

        @Suppress("UNCHECKED_CAST")
        return template.execute { block() } as R
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : AkoModel> save(data: T): T {
        val context = contextOf(data.javaClass)
        context.akoPreSave(data)
        context.save(data)
        context.akoPostSave(data)
        return data
    }

    fun delete(modelType: Class<out AkoModel>, id: Any?) {
        requireNotNull(id) { "模型 ${modelType.simpleName} 的 id 不能为空" }
        contextOf(modelType).delete(id.toString())
    }

    private fun contextOf(type: Class<*>): ModelContext<AkoModel> {
        @Suppress("UNCHECKED_CAST")
        return modelMap.values.firstOrNull {
            it.type == type || it.type.isAssignableFrom(type)
        } as? ModelContext<AkoModel>
            ?: error("无法找到模型: ${type.name}")
    }

    private fun initializeModels() {
        if (initializedModelMap != null) return

        synchronized(this) {
            if (initializedModelMap != null) return

            val owners = LinkedHashMap<Class<out AkoModel>, AkoSpringDatabase>()
            databases.forEach { database ->
                database.modelTypes.forEach { modelType ->
                    if (modelType.isAnnotationPresent(NoAkoModel::class.java)) return@forEach
                    val previous = owners.put(modelType, database)
                    check(previous == null) {
                        "模型 ${modelType.name} 同时被多个 AkoSpringDatabase 提供"
                    }
                }
            }

            initializedModelMap = owners.map { (modelType, database) ->
                @Suppress("UNCHECKED_CAST")
                val typedModelType = modelType as Class<AkoModel>
                val model = typedModelType.dbModel
                model.id to AkoSpringModelContext(
                    name = model.id,
                    type = typedModelType,
                    model = model,
                    database = database,
                )
            }.toMap()
        }
    }
}

/** Creates a lazy proxy suitable for Kotlin companion-object delegation. */
fun <T : AkoAccess<*, *>> createAccessProxy(type: Class<T>): T {
    val handler = InvocationHandler { proxy, method, args ->
        if (method.declaringClass == Any::class.java) {
            when (method.name) {
                "toString" -> "AkoAccessProxy(${type.name})"
                "hashCode" -> System.identityHashCode(proxy)
                "equals" -> proxy === args?.firstOrNull()
                else -> null
            }
        } else {
            val target = AkoSpringRuntime.instance.accessOf(type)
            try {
                method.invoke(target, *(args ?: emptyArray()))
            } catch (error: java.lang.reflect.InvocationTargetException) {
                throw (error.targetException ?: error)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    return Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler) as T
}
