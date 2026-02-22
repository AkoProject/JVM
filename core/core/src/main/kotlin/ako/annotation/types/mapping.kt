package ako.annotation.types

import ako.AkoService
import ako.annotation.AkoType
import ako.model.base.AkoModel
import ako.model.base.ModelContext
import ako.protocol.db.DbField
import ako.protocol.db.DbModel
import ako.protocol.type.AkoTypeProvider
import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnore
import java.lang.reflect.Field
import kotlin.reflect.KClass

/*** 关联字段
 * @param value 关联的实体类
 * @param field 关联的字段
 * @param display 显示的字段
 * @param flag 仅用于 CascadeMapping，指定级联值。
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@AkoType(MappingTypeProvider::class)
annotation class Mapping(
    val value: KClass<out AkoModel>,
    val field: String = "id",
    val display: String,
    val flag: String = ""
)

/*** 级联关联
 * @param field 关联枚举字段
 * @param mappings 关联映射，如果 Mapping 的 index 保持 -1，则会按数组下标自动对应。
 */
@Target(AnnotationTarget.FIELD)
@AkoType(MappingTypeProvider::class)
annotation class CascadeMapping(val field: String, vararg val mappings: Mapping)

data class MappingTarget(
    val model: String,
    val field: String,
    val display: String
)

data class MappingOption(
    val cascader: String?,
    val values: Map<String, MappingTarget>
)

class MappingTypeProvider : AkoTypeProvider<Annotation, MappingOption, HashMap<String, ArrayList<Any>>> {
    override val id: String
        get() = "ako:mapping"

    override fun readField(
        model: String,
        id: String,
        fieldType: Class<*>,
        nullable: Boolean,
        modelClass: Class<*>?,
        fieldInstance: Field?,
        hitAnnotation: Annotation?,
        typeAnnotation: AkoType?
    ): MappingOption? {
        if (hitAnnotation is Mapping) return MappingOption(
            null,
            mapOf(
                "__blank__" to MappingTarget(
                    hitAnnotation.value.simpleName!!,
                    hitAnnotation.field,
                    hitAnnotation.display
                )
            )
        )
        if (hitAnnotation is CascadeMapping) return MappingOption(
            hitAnnotation.field,
            hitAnnotation.mappings.mapIndexed { index, mapping ->
                (mapping.flag.takeIf { it.isNotEmpty() } ?: index.toString()) to MappingTarget(
                    mapping.value.simpleName!!,
                    mapping.field,
                    mapping.display
                )
            }.toMap()
        )
        return null
    }

    override fun searchInformation(
        ctx: ModelContext<*>,
        model: DbModel<*>,
        field: DbField,
        opt: MappingOption?,
        ret: HashMap<String, ArrayList<Any>>?,
        data: List<Any?>
    ): HashMap<String, ArrayList<Any>>? {
        if (opt == null) return ret
        val ret = ret ?: HashMap()
        val needRead = HashMap<String, MutableSet<String>>()

        data.forEach {
            val value = field[it!!] ?: return@forEach
            val mapping = opt.values[opt.cascader ?: "__blank__"] ?: return@forEach
            needRead.getOrPut("${mapping.model}:${mapping.field}") { HashSet() }.add(value.toString())
        }

        needRead.forEach { (mCtx, ids) ->
            val (mModelId, mFieldId) = mCtx.split(":")
            val mCtx = AkoService.modelOf(mModelId)
            val mModel = mCtx.model
            val mField = mModel.fields.find { it.id == mFieldId }
                ?: error("模型 $mModelId 中不存在字段 $mFieldId！")

            val curr = ret.getOrPut(mModelId) { ArrayList() }
            val currHas = curr.mapNotNull { mField[it]?.toString() }.toHashSet()
            ids.removeAll(currHas)
            if (ids.isEmpty()) return@forEach

            mCtx.whereList(mapOf("${mField.id}_in" to ids)).let { curr.addAll(it) }
        }

        return ret
    }
}