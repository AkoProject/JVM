package ako.protocol.db

import ako.protocol.base.BaseField
import ako.protocol.edit.EditField
import ako.protocol.edit.EditInfo
import ako.protocol.search.SearchField
import ako.protocol.search.SearchInfo
import ako.protocol.table.ColumnInfo
import ako.protocol.table.TableField
import ako.protocol.type.AkoTypeProvider
import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnore
import java.lang.reflect.Field
import java.util.UUID

data class DbField(
    @field:JsonIgnore
    @field:JSONField(serialize = false)
    val field: Field,

    override val id: String,
    override val name: String,
    override val description: String?,

    override val search: SearchInfo?,
    override val edit: EditInfo?,
    override val column: ColumnInfo?,

    override val type: String,
    override val options: Any?,
    @field:JsonIgnore
    @field:JSONField(serialize = false)
    val provider: AkoTypeProvider<*, *, *>?,
) : BaseField, SearchField, EditField, TableField {
    operator fun get(instance: Any): Any? = field.apply { isAccessible = true }.get(instance)

    fun convert(value: Any?): Any? {
        if (value == null) return null
        if (value is Collection<*>) return value.mapNotNull { convert(it) }
        val fieldType = field.type
        if (fieldType == String::class.java) return value.toString()
        if (fieldType == Boolean::class.javaPrimitiveType || fieldType == Boolean::class.javaObjectType) {
            if (value is Boolean) return value
            if (value is String) return value == "1" || value == "true"
            if (value is Number) return value == 1
        }
        if (value is Number) return value
        if (value is String)
            return when (fieldType) {
                Byte::class.javaPrimitiveType, Byte::class.javaObjectType -> value.toByte()
                Short::class.javaPrimitiveType, Short::class.javaObjectType -> value.toShort()
                Int::class.javaPrimitiveType, Int::class.javaObjectType -> value.toInt()
                Long::class.javaPrimitiveType, Long::class.javaObjectType -> value.toLong()
                Float::class.javaPrimitiveType, Float::class.javaObjectType -> value.toFloat()
                Double::class.javaPrimitiveType, Double::class.javaObjectType -> value.toDouble()
                UUID::class.java -> UUID.fromString(value)
                else -> value
            }
        return value
    }

}