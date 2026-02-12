package ako.rain.model

import ako.access.AkoAccess
import ako.model.DbModel
import ako.model.ModelContext
import ako.model.db.AkoModel
import ako.model.resp.PageResp
import smartaccess.item.Page

class AkoRainModelContext<T : AkoModel>(
    override val name: String,
    override val type: Class<out T>,
    override val model: DbModel<out T>,
    val access: AkoAccess<T>
) : ModelContext<T> {

    companion object {
        fun <T : AkoModel> create(
            name: String,
            model: DbModel<out T>,
            access: AkoAccess<*>
        ) = AkoRainModelContext(name, model.type, model, access as AkoAccess<T>)
    }

    override fun whereList(
        params: Map<String, Any>?,
        pid: Int?,
        pSize: Int?
    ): List<T> {
        return if (params == null)
            if (pid != null && pSize != null) access.findAll(Page((pid - 1) * pSize, pSize))
            else access.findAll()
        else if (pid != null && pSize != null) access.whereQuery(params, Page((pid - 1) * pSize, pSize))
        else access.whereQuery(params)
    }

    val convertMap = model.fields.associate { it.id to it::convert }

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

    override fun delete(id: Int) = access.delete(id)

}