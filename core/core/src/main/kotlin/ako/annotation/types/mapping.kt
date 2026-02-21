package ako.annotation.types

import ako.annotation.AkoType
import ako.model.base.AkoModel
import ako.protocol.type.AkoTypeProvider
import java.lang.reflect.Field
import kotlin.reflect.KClass

/*** 关联字段
 * @param value 关联的实体类
 * @param field 关联的字段
 * @param display 显示的字段
 * @param index 仅用于 EnumMapping，指定枚举值的下标。
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class Mapping(
    val value: KClass<out AkoModel>,
    val field: String = "id",
    val display: String,
    val index: Int = -1
)

/*** 动态解析 mapping 关联
 * @param field 关联枚举字段
 * @param mappings 关联映射，如果 Mapping 的 index 保持 -1，则会按数组下标自动对应。
 */
@Target(AnnotationTarget.FIELD)
annotation class EnumMapping(val field: String, vararg val mappings: Mapping)

data class MappingTarget(
    val model: String,
    val field: String,
    val display: String,
)

data class MappingOption(
    val enum: String?,
    val mapping: Mapping
)

//class MappingTypeProvider : AkoTypeProvider<Annotation, MappingOption, Map<String, Any>> {
//    override val id: String
//        get() = "ako:mapping"
//
//    override fun readField(
//        model: String,
//        id: String,
//        fieldType: Class<*>,
//        nullable: Boolean,
//        modelClass: Class<*>?,
//        fieldInstance: Field?,
//        hitAnnotation: Annotation?,
//        typeAnnotation: AkoType?
//    ): MappingOption? {
//
//    }
//}