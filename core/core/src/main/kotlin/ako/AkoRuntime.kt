package ako

import ako.model.base.ModelContext
import ako.protocol.type.AkoTypeProvider

interface AkoRuntime {

    val modelMap: Map<String, ModelContext<*>>
    fun <T: AkoTypeProvider<*,*,*>> getTypeProvider(providerType: Class<T>): T?

}