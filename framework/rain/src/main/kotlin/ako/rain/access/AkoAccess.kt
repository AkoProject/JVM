package ako.access

import ako.model.base.AkoModel
import smartaccess.annotation.MetadataProvider
import smartaccess.annotation.ProvideAccessTemple
import smartaccess.item.Page
import smartaccess.item.PageResult
import smartaccess.jpa.access.JpaAccess

@ProvideAccessTemple
@MetadataProvider(AkoMetadataProvider::class)
interface AkoAccess<T : AkoModel> : JpaAccess<T, Int> {

    companion object {
        fun margeWhereQuery(
            query: StringBuilder,
            paras: Map<String, Any?>,
            converts: Map<String, (Any?) -> Any?>? = null
        ): Array<Any?> {
            paras["deleteTime-isNull"]
            val paramList = ArrayList<Any?>()
            paras.forEach { (k, v) ->

                var name = k
                var opt = "=" to true

                if (name.contains("_")) {
                    val split = name.split("_", limit = 2)
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
                        else -> split[1] to (v != null)
                    }
                }

                if (opt.second) run { converts?.get(name)?.invoke(v) ?: v }?.let { paramList.add(it) }
                query.append(" and $name ${opt.first} ${if (opt.second) "?${paramList.size}" else ""}")
            }
            return paramList.toTypedArray()
        }
    }

    fun deleteAll() {
        execute("delete from $modelName")
    }

    fun findFirst(paras: Map<String, Any> = emptyMap(), page: Page = Page(0, 10)) =
        whereQuery(paras, page, "ASC")

    fun findLast(paras: Map<String, Any> = emptyMap(), page: Page = Page(0, 10)) =
        whereQuery(paras, page, "DESC")

    fun countAll(): Int {
        return jpaQuery("select count(id) from $modelName").singleResult as Int
    }

    fun countQuery(paras: Map<String, Any?>): Long {
        val queryBuilder = StringBuilder("select count(id) from $modelName where 1=1")
        val paramList = margeWhereQuery(queryBuilder, paras)

        return count(queryBuilder.toString(), *paramList)
    }

    fun whereQuery(paras: Map<String, Any?>, orderBy: String = "ASC"): List<T> {
        val queryBuilder = StringBuilder("from $modelName where 1=1")
        val paramList = margeWhereQuery(queryBuilder, paras)

        queryBuilder.append(" order by id $orderBy")

        return list(queryBuilder.toString(), *paramList)
    }

    fun whereQuery(paras: Map<String, Any?>, page: Page = Page(0, 10), orderBy: String = "ASC"): List<T> {
        val queryBuilder = StringBuilder("from $modelName where 1=1")
        val paramList = margeWhereQuery(queryBuilder, paras)

        queryBuilder.append(" order by id $orderBy")

        return list(queryBuilder.toString(), page = page, *paramList)
    }

    fun whereQuery(
        paras: Map<String, Any?>,
        sort: Map<String, String>,
        page: Page = Page(0, 10),
        orderBy: String = "ASC"
    ): List<T> {
        val queryBuilder = StringBuilder("from $modelName where 1=1")
        val paramList = margeWhereQuery(queryBuilder, paras)

        if (sort.isEmpty()) queryBuilder.append(" order by id $orderBy")
        else {
            queryBuilder.append(" order by ")
            sort.forEach { (k, v) -> queryBuilder.append("$k $v,") }
            queryBuilder.deleteCharAt(queryBuilder.length - 1)
        }

        return list(queryBuilder.toString(), page = page, *paramList)
    }

    fun wherePage(
        paras: Map<String, Any?>,
        page: Page = Page(0, 10),
        orderBy: String = "ASC",
        sort: Map<String, String>? = null,
        converts: Map<String, (Any?) -> Any?>? = null
    ): PageResult<T> {
        val queryBuilder = StringBuilder("from $modelName where 1=1")
        val paramList = margeWhereQuery(queryBuilder, paras, converts)
        val query = queryBuilder.toString()

        sort?.takeIf { it.isNotEmpty() }?.let {
            queryBuilder.append(" order by ")
            sort.forEach { (k, v) -> queryBuilder.append("$k $v,") }
            queryBuilder.deleteCharAt(queryBuilder.length - 1)
        } ?: queryBuilder.append(" order by id $orderBy")
//        if (sort.isEmpty()) queryBuilder.append(" order by id $orderBy")
//        else {
//            queryBuilder.append(" order by ")
//            sort.forEach { (k, v) -> queryBuilder.append("$k $v,") }
//            queryBuilder.deleteCharAt(queryBuilder.length - 1)
//        }

        return PageResult(
            count("select count(id) $query", *paramList),
            list(query, page = page, *paramList)
        )
    }

}