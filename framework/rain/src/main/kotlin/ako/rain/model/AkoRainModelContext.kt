package ako.rain.model

import ako.rain.access.AkoAccess
import ako.model.base.ModelContext
import ako.model.base.AkoModel
import ako.model.resp.PageResp
import ako.protocol.db.DbModel
import smartaccess.item.Page
import java.io.Serializable

class AkoRainModelContext<T : AkoModel, PK: Serializable>(
    override val name: String,
    override val type: Class<out T>,
    override val model: DbModel<out T>,
    val access: AkoAccess<T, PK>
) : ModelContext<T> {

    companion object {
        fun <T : AkoModel> create(
            name: String,
            model: DbModel<out T>,
            access: AkoAccess<*, *>
        ) = AkoRainModelContext(name, model.type, model, access as AkoAccess<T, *>)
    }

    val convertMap = model.fields.associate { it.id to it::convert }

    override fun whereList(
        params: Map<String, Any>?,
        pid: Int?,
        pSize: Int?
    ): List<T> {
        return if (params == null)
            if (pid != null && pSize != null) access.findAll(Page((pid - 1) * pSize, pSize))
            else access.findAll()
        else if (pid != null && pSize != null)
            access.whereQuery(params, Page((pid - 1) * pSize, pSize), converts = convertMap)
        else access.whereQuery(params, converts = convertMap)
    }

    override fun wherePage(
        params: Map<String, Any>?,
        sort: Map<String, String>?,
        pid: Int,
        pSize: Int
    ): PageResp<T> = access.wherePage(
        params ?: emptyMap(),
        Page((pid - 1) * pSize, pSize),
        sort = sort,
        converts = convertMap,
    ).let { PageResp(pid, pSize, it.total.toInt(), it.data) }

    override fun akoPreSave(data: T) {

    }

    override fun akoPostSave(data: T) {

    }

    override fun save(data: T) = access.saveOrUpdate(data)

    override fun delete(id: String) = access.delete(model.idField.convert(id) as PK)

}