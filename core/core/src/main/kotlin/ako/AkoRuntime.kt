package ako

import ako.model.base.ModelContext

interface AkoRuntime {

    val modelMap: Map<String, ModelContext<*>>

}