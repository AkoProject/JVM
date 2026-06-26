package ako.model.base

import ako.model.resp.PageResp
import ako.protocol.db.DbModel

interface ModelContext<T : AkoModel> {
    val name: String
    val type: Class<out T>
    val model: DbModel<out T>

    fun whereList(
        params: Map<String, Any>? = null,
        pid: Int? = null,
        pSize: Int? = null
    ): List<T>

    fun wherePage(
        params: Map<String, Any>? = null,
        sort: Map<String, String>? = null,
        pid: Int,
        pSize: Int
    ): PageResp<T>

    fun akoPreSave(data: T)
    fun akoPostSave(data: T)

    fun save(data: T)
    fun delete(id: String)

}