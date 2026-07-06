package ako

import ako.annotation.MenuGroup
import ako.annotation.types.EnumTypeProvider
import ako.`fun`.webError
import ako.protocol.DbModelMenu
import ako.model.base.ModelContext
import ako.protocol.type.AkoDefaultTypeProvider
import ako.protocol.type.AkoTypeProvider
import ako.protocol.type.InternalDefaultTypeProviderContainer

object AkoService {


    private val defaultProviders = ArrayList<InternalDefaultTypeProviderContainer>()

    fun registerDefaultTypeProvider(priority: Int, provider: AkoDefaultTypeProvider) {
        defaultProviders.add(InternalDefaultTypeProviderContainer(priority, provider))
        defaultProviders.sortBy { it.priority }
    }

    fun findDefaultTypeProvider(fieldType: Class<*>): AkoTypeProvider<*, *, *>? {
        for (container in defaultProviders) {
            val provider = container.provider(fieldType)
            if (provider != null) return provider
        }
        return null
    }

    lateinit var runtime: AkoRuntime
        private set

    fun initRuntime(runtime: AkoRuntime) {
        this.runtime = runtime
        registerDefaultTypeProvider(100, AkoDefaultTypeProvider { fieldType ->
            if (fieldType.isEnum || fieldType == Boolean::class.javaObjectType || fieldType == Boolean::class.javaPrimitiveType)
                AkoService.runtime.getTypeProvider(EnumTypeProvider::class.java)
            else null
        })
    }

    val dbMenus = HashMap<String, DbModelMenu>()

    fun findMenu(annotation: MenuGroup): DbModelMenu {
        val id = annotation.id
        if (id == "") error("菜单组的 id 不能为空！")

        val menu = dbMenus.getOrPut(id) {
            DbModelMenu(
                channel = "",
                identifier = id,
                iconNode = "default-entity-icon-node"
            )
        }
        if (annotation.name != "") menu.name = annotation.name
        if (annotation.icon != "") menu.iconNode = annotation.icon
        if (annotation.index != 0) menu.index = annotation.index
        return menu
    }

    fun modelOf(modelName: String): ModelContext<*> =
        runtime.modelMap[modelName] ?: webError(881001001, "模型 $modelName 不存在")

}