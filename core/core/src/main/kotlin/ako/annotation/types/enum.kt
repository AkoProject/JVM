package ako.annotation.types

import ako.annotation.AkoType
import ako.protocol.type.AkoTypeProvider
import java.lang.reflect.Field

/*** 枚举类型
 * @param value 枚举值
 * @param flag 该值将作为 CascadeEnum 的标志位，空字符串按做下标处理。在非 CascadeEnum 中使用时，该项无任何意义。
 *
 * 默认会按下标对应枚举值中配置的名字，如果想指定下标和名字，请使用 "值:名字" 的格式。
 */
@Target(AnnotationTarget.FIELD)
@AkoType(EnumTypeProvider::class)
annotation class DbEnum(vararg val value: String, val flag: String = "")

/*** 关联枚举类型
 * @param cascader 关联枚举字段
 * @param value 关联枚举值，如果 DbEnum 的 flag 保持 空字符串，则会按数组下标自动对应。
 */
@Target(AnnotationTarget.FIELD)
@AkoType(EnumTypeProvider::class)
annotation class CascadeEnum(val cascader: String, vararg val value: DbEnum)

data class EnumElement(
    val value: String,
    val label: String,
)

class EnumOptions(
    val cascader: String?,
    val values: Map<String, List<EnumElement>>,
)

class EnumTypeProvider : AkoTypeProvider<Annotation, EnumOptions, Any> {
    override val id: String
        get() = "ako:enum"

    fun Array<out String>.toEnumList() = mapIndexed { i, s ->
        run { if (s.contains(":")) s.split(":").let { it[0] to it[1] } else i.toString() to s }
            .let { EnumElement(it.first, it.second) }
    }

    override fun readField(
        model: String,
        id: String,
        fieldType: Class<*>,
        nullable: Boolean,
        modelClass: Class<*>?,
        fieldInstance: Field?,
        hitAnnotation: Annotation?,
        typeAnnotation: AkoType?
    ): EnumOptions? {
        if (hitAnnotation is DbEnum) return EnumOptions(null, mapOf("__blank__" to hitAnnotation.value.toEnumList()))
        if (hitAnnotation is CascadeEnum) return EnumOptions(
            hitAnnotation.cascader,
            hitAnnotation.value.mapIndexed { index, dbEnum ->
                (dbEnum.flag.takeIf { it.isNotEmpty() } ?: index.toString()) to dbEnum.value.toEnumList()
            }.toMap()
        )
        return null
    }
}