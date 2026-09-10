package ako.spring.`fun`

import ako.model.base.AkoModel
import ako.spring.AkoSpringRuntime
import ako.spring.access.AkoAccess
import ako.spring.createAccessProxy

inline fun <reified T : AkoAccess<*, *>> findAccess(): T = createAccessProxy(T::class.java)

inline fun <reified T : AkoModel> T.save(): T = AkoSpringRuntime.instance.save(this)

inline fun <reified T : AkoModel> T.delete() {
    val modelContext = AkoSpringRuntime.instance.modelMap.values.firstOrNull {
        it.type == T::class.java || it.type.isAssignableFrom(this.javaClass)
    } ?: error("无法找到模型: ${T::class.java.name}")
    val id = modelContext.model.idField[this]
        ?: error("模型 ${modelContext.name} 的 id 不能为空")
    AkoSpringRuntime.instance.delete(T::class.java, id)
}
