package ako.spring.model

import ako.model.base.AkoModel
import ako.model.base.ModelContext
import ako.model.resp.PageResp
import ako.protocol.db.DbModel
import ako.spring.db.AkoSpringDatabase

/** Bridges Ako's model metadata to a Spring database SPI implementation. */
class AkoSpringModelContext<T : AkoModel>(
    override val name: String,
    override val type: Class<out T>,
    override val model: DbModel<out T>,
    private val database: AkoSpringDatabase,
) : ModelContext<T> {

    override fun whereList(
        params: Map<String, Any>?,
        pid: Int?,
        pSize: Int?,
    ): List<T> = database.whereList(model, params, null, pid, pSize)

    override fun wherePage(
        params: Map<String, Any>?,
        sort: Map<String, String>?,
        pid: Int,
        pSize: Int,
    ): PageResp<T> = database.wherePage(model, params, sort, pid, pSize)

    override fun akoPreSave(data: T) = Unit

    override fun akoPostSave(data: T) = Unit

    override fun save(data: T) {
        database.save(data)
    }

    override fun delete(id: String) {
        database.delete(model, model.idField.convert(id))
    }
}
