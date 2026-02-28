package ako

import ako.annotation.MenuGroup
import ako.`fun`.webError
import ako.protocol.DbModelMenu
import ako.model.base.ModelContext

object AkoService {

    lateinit var runtime: AkoRuntime
        private set

    fun initRuntime(runtime: AkoRuntime) {
        this.runtime = runtime
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