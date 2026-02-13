package ako.`fun`

import ako.model.base.ModelContext
import ako.model.base.AkoModel
import ako.model.resp.PageResp
import jakarta.persistence.Query
import jakarta.persistence.TypedQuery

fun buildWhere(params: Map<String, Any>): String {
    val whereBuilder = StringBuilder("where 1=1 ")
    params.forEach { (k, _) ->
        var name = k
        var opt = "=" to true

        if (k.contains("_")) {
            val split = k.split("_")
            name = split[0]
            opt = when (split[1]) {
                "eq" -> "=" to true
                "gt" -> ">" to true
                "lt" -> "<" to true
                "gte" -> ">=" to true
                "lte" -> "<=" to true
                "ne" -> "!=" to true
                "like" -> "like" to true
                "in" -> "in" to true
                "isNull" -> "is null" to false
                else -> error("不支持的操作符：${split[1]}")
            }
        }
        whereBuilder.append("AND $name ${opt.first} ")
        if (opt.second) whereBuilder.append(":$k ")
    }
    return whereBuilder.toString()
}

fun <T : AkoModel> whereList(
    model: ModelContext<T>,
    params: Map<String, Any>? = null,
    sort: Map<String, String>? = null,
    pid: Int? = null,
    pSize: Int? = null,
    createQuery: (String, Class<out T>?) -> TypedQuery<out T>
): List<T> {
    val where = params?.let { buildWhere(it) }
    val name = model.name

    val sort = sort?.let {
        it.entries.joinToString(prefix = "ORDER BY ", separator = ", ") { (key, value) -> "$key $value" }
    } ?: "ORDER BY a.id DESC"

    val searchQuery = createQuery("FROM $name a ${where ?: ""} $sort", model.type)
    params?.forEach { (k, v) ->
        searchQuery.setParameter(k, v)
    }
    if (pid != null && pSize != null) {
        searchQuery.firstResult = (pid - 1) * pSize
        searchQuery.maxResults = pSize
    }
    return searchQuery.resultList
}

fun <T : AkoModel> wherePage(
    model: ModelContext<T>,
    params: Map<String, Any>? = null,
    sort: Map<String, String>? = null,
    pid: Int,
    pSize: Int,
    createQuery: (String, Class<out T>?) -> Query
): PageResp<T> {
    val where = params?.let { buildWhere(it) }
    val name = model.name

    val sort = sort?.let {
        it.entries.joinToString(prefix = "ORDER BY ", separator = ", ") { (key, value) -> "$key $value" }
    } ?: "ORDER BY a.id DESC"

    val searchQuery = createQuery("FROM $name a ${where ?: ""} $sort", model.type) as TypedQuery<T>
    val countQuery = createQuery("SELECT COUNT(*) FROM $name a ${where ?: ""}", null)
    params?.forEach { (k, v) ->
        searchQuery.setParameter(k, v)
        countQuery.setParameter(k, v)
    }

    searchQuery.firstResult = (pid - 1) * pSize
    searchQuery.maxResults = pSize

    return PageResp(pid, pSize, (countQuery.singleResult as Long).toInt(), searchQuery.resultList)
}