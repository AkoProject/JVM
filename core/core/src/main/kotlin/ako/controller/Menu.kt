package ako.controller

import ako.AkoService
import ako.annotation.NoAkoModel
import ako.`fun`.hasAnnotation
import kotlin.collections.filter

object Menu {

    fun list(channel: String): Map<String, Any> {
        return mapOf(
            "menus" to AkoService.dbMenus.values,
            "models" to AkoService.runtime.modelMap.values
                .filter { !it.type.hasAnnotation<NoAkoModel>() }
                .map { it.model }
        )
    }

}