package ako

import ako.model.ModelContext

interface AkoRuntime {

    val modelMap: Map<String, ModelContext<*>>

}