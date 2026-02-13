package ako.controller

import ako.AkoService
import ako.model.base.ModelContext
import ako.model.base.AkoModel
import ako.model.req.ModelPageReq
import ako.model.resp.ModelPageResp

object Model {

    fun page(model: String, data: ModelPageReq): ModelPageResp {
        val modelContext = AkoService.modelOf(model)

        val all = modelContext.wherePage(data.params, data.sort, data.page, data.size)
        val mappings = HashMap<String, MutableList<Any>>()
        modelContext.model.mappings.forEach { (name, mapping) ->
            AkoService.modelOf(name)
                .whereList(mapOf("${mapping.fieldName}_in" to all.list.mapNotNull { e -> mapping.field(e) }))
                .let { mappings.getOrPut(name) { ArrayList() }.addAll(it) }
        }

        return ModelPageResp(all.total, all.list, mappings)
    }

    fun save(model: String, data: AkoModel) {
        val modelContext = AkoService.modelOf(model) as ModelContext<AkoModel>

        modelContext.akoPreSave(data)
        modelContext.save(data)
        modelContext.akoPostSave(data)
    }

    fun delete(model: String, ids: List<Int>) {
        val modelContext = AkoService.modelOf(model)
        ids.forEach { modelContext.delete(it) }
    }

}