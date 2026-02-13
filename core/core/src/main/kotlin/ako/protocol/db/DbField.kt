package ako.protocol.db

import ako.model.protocol.table.ColumnInfo
import ako.model.protocol.edit.EditInfo
import ako.model.protocol.search.SearchInfo
import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnore
import java.lang.reflect.Field

data class DbField(
    @field:JsonIgnore
    @field:JSONField(serialize = false)
    val field: Field,

    val id: String,
    val name: String,
    val description: String?,

    val search: SearchInfo?,
    val edit: EditInfo?,
    val column: ColumnInfo?,

    val type: Int,
    val subtype: Int,

    val content: String?,
    val enum: List<String>?,
) {
    operator fun get(instance: Any): Any? = field.apply { isAccessible = true }.get(instance)

    fun convert(value: Any?): Any? {
        if (value == null) return null
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
                else -> value
            }
        return value
    }

}