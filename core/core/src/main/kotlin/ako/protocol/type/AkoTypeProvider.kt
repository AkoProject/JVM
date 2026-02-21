package ako.protocol.type

import ako.annotation.AkoType
import ako.protocol.db.DbField
import ako.protocol.db.DbModel
import ako.protocol.search.SearchEntry
import java.lang.reflect.Field

interface AkoTypeProvider<A : Annotation, OPT, RET> {

    companion object {
        fun AkoTypeProvider<*, *, *>.cast2Any() = this as AkoTypeProvider<Annotation, Any, Any>
    }

    val id: String

    fun defaultSearch(
        model: String,
        id: String,
        fieldType: Class<*>,
        width: String,
        opt: OPT? = null,
    ): List<SearchEntry>? = null

    fun readField(
        model: String,
        id: String,
        fieldType: Class<*>,
        nullable: Boolean,
        modelClass: Class<*>? = null,
        fieldInstance: Field? = null,
        hitAnnotation: A? = null,
        typeAnnotation: AkoType? = null,
    ): OPT? = null

    fun searchInformation(
        model: DbModel<*>,
        field: DbField,
        opt: OPT?,
        ret: RET?,
        data: List<Any?>,
    ): RET? = null

}