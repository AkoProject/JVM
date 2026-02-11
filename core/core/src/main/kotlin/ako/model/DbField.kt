package ako.model

import com.alibaba.fastjson2.annotation.JSONField
import java.lang.reflect.Field

data class DbField(
    @JSONField(serialize = false)
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
}