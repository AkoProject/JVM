package ako.`fun`

import ako.AkoService
import ako.annotation.*
import ako.annotation.ModelButton.Companion.toModelButton
import ako.annotation.OperateButton.Companion.toModelButton
import ako.protocol.button.ButtonEditInfo
import ako.protocol.db.DbField
import ako.protocol.button.ButtonEntry
import ako.protocol.table.ColumnInfo
import ako.protocol.db.DbModel
import ako.protocol.edit.EditInfo
import ako.protocol.edit.EditValidateEntry
import ako.protocol.search.SearchEntry
import ako.protocol.search.SearchInfo
import ako.model.base.AkoModel
import ako.protocol.db.CustomDbField
import ako.protocol.type.AkoTypeProvider
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import kotlin.reflect.jvm.kotlinProperty

object AkoDefaultNode {

    var view = "default-entity-page-node"
    var icon = "default-entity-icon-node"

    var entitySearch = "default-entity-search-node"
    var entityTable = "default-entity-table-node"
    var entityEdit = "default-entity-edit-node"

    var fieldSearchInput = "default-entity-search-input-node"
    var fieldSearchProperty = "default-entity-search-property-node"
    var fieldEditInput = "default-entity-edit-input-node"
    var fieldEditProperty = "default-entity-edit-property-node"
    var fieldTableColumn = "default-entity-table-column-node"
}

val <T : AkoModel> Class<T>.dbModel: DbModel<T>
    get() {
        val modelButtons = ArrayList<ButtonEntry>()

        val modelButtonsAnnotation = annotation<ModelButtons>()
        if (modelButtonsAnnotation == null) {
            modelButtons.add(
                ButtonEntry(
                    index = 0,
                    name = "查询",
                    eval = "await props.search()",
                    type = "primary"
                )
            )
            modelButtons.add(
                ButtonEntry(
                    index = 1,
                    name = "新增",
                    edit = ButtonEditInfo(),
                    type = "default"
                )
            )
            modelButtons.add(
                ButtonEntry(
                    index = 2,
                    name = "批量删除",
                    eval = "await props.api.model.delete(props.model.id, multi.map(it => it.id));" +
                            "await props.search()",
                    type = "danger",
                    reconfirm = "确定要删除选中的项吗？"
                )
            )

            kotlin.annotations.filterIsInstance<ModelButton>()
                .map { it.toModelButton() }
                .let { modelButtons.addAll(it) }

        } else modelButtons.addAll(modelButtonsAnnotation.value.map { it.toModelButton() })

        val operateButtons = ArrayList<ButtonEntry>()

        val operateButtonsAnnotation = annotation<OperateButtons>()
        if (operateButtonsAnnotation == null) {
            operateButtons.add(
                ButtonEntry(
                    index = 0,
                    name = "编辑",
                    type = "primary",
                    edit = ButtonEditInfo()
                )
            )
            operateButtons.add(
                ButtonEntry(
                    index = 1,
                    name = "删除",
                    eval = "await props.api.model.delete(props.model.id, [single.id]);" +
                            "await props.search()",
                    type = "danger",
                    reconfirm = "确定要删除选中的项吗？"
                )
            )

            annotations.filterIsInstance<OperateButton>()
                .map { it.toModelButton() }
                .let { operateButtons.addAll(it) }
        } else operateButtons.addAll(operateButtonsAnnotation.value.map { it.toModelButton() })



        return DbModel(
            this,
            simpleName,
            annotation<DbName>()?.value ?: simpleName,
            annotation<MenuGroup>()?.let { AkoService.findMenu(it) }?.identifier,
            null,
            annotation<DbName>()?.index ?: 100,
            false,
            annotation<ModelNode>()?.pageNode ?: AkoDefaultNode.view,
            annotation<ModelNode>()?.searchNode ?: AkoDefaultNode.entitySearch,
            annotation<ModelNode>()?.tableNode ?: AkoDefaultNode.entityTable,
            annotation<ModelNode>()?.editNode ?: AkoDefaultNode.entityEdit,
            annotation<ModelNode>()?.iconNode ?: AkoDefaultNode.icon,
            modelButtons,
            operateButtons
        )
    }

fun AnnotatedElement.readFieldInfo(
    model: String,
    id: String,
    fieldType: Class<*>,
    nullable: Boolean,
    modelClass: Class<*>? = null,
    fieldInstance: Field? = null,
): CustomDbField {
    val name = annotation<DbName>()?.value ?: id

    val searchIgnore = hasAnnotation<SearchIgnore>()
    val tableIgnore = hasAnnotation<TableIgnore>()
    val editIgnore = hasAnnotation<EditIgnore>()

    var type = "ako:text"
    var option: Any?

    var hitAnnotation: Annotation? = null
    var typeAnnotation: AkoType? = null
    var typeProvider: AkoTypeProvider<Annotation, Any, Any>? = null


    fun hitAnnotation(hit: Annotation?, type: AkoType) {
        if (typeAnnotation != null) error("字段 $model.$id 上存在多个 AkoType 注解！提供的 AkoType 注解必须唯一！")
        hitAnnotation = hit
        typeAnnotation = type

        typeProvider =
            AkoService.runtime.getTypeProvider(typeAnnotation.provider.java) as? AkoTypeProvider<Annotation, Any, Any>?
                ?: error("字段 $model.$id 上的 AkoType 注解 ${typeAnnotation.provider.java.name} 无法加载对应的实例！")
    }

    declaredAnnotations.forEach {
        if (it !is AkoType) it.annotationAnnotation<AkoType>()
            .forEach { at -> hitAnnotation(it, at) }
        else hitAnnotation(null, it)
    }

    option = typeProvider?.readField(
        model,
        id,
        fieldType,
        nullable,
        modelClass,
        fieldInstance,
        hitAnnotation,
        typeAnnotation
    )
    type = typeProvider?.id ?: hitAnnotation?.annotationAnnotation<Identifier>()?.firstOrNull()?.value ?: type

    val searchWidth = annotation<SearchColumnWidth>()?.value ?: "200px"
    val tableWidth = annotation<TableColumnWidth>()?.value ?: 200

    val searchEntries = annotation<SearchType>()?.value
        ?.map { SearchEntry(AkoDefaultNode.fieldSearchInput, it.opt, name, searchWidth) }
        ?: typeProvider?.defaultSearch(model, id, fieldType, searchWidth, option)
        ?: listOf(SearchEntry(AkoDefaultNode.fieldSearchInput, "eq", name, searchWidth))

    fun editInfo(): EditInfo? {
        if (editIgnore) return null
        val validates = ArrayList<EditValidateEntry>()
        var required = nullable
        val allowEmpty = hasAnnotation<AllowEmpty>()
        run {
            var message: String? = null
            annotation<Required> {
                required = true
                message = value
            }
            if (message == null && !nullable) message = "$name 不能为空"

            if (message != null && !allowEmpty)
                validates.add(EditValidateEntry(require = true, message = message))
        }
        annotation<Required> {
            required = true
            validates.add(EditValidateEntry(require = true, message = value))
        } ?: run {
            if (!nullable) {
                required = true
                validates.add(EditValidateEntry(require = true, message = "$name 不能为空"))
            }
        }
        annotation<RegExpValidate> {
            validates.add(EditValidateEntry(regexp = value, message = message.ifEmpty { "$name 内容不合法" }))
        }
        annotation<RangeValidate> {
            if (min == -1 && max == -1) return@annotation
            if (min < max) error("字段 $model.$id 的 Range 声明不正确，$min 不能大于 $max！")
            validates.add(
                EditValidateEntry(
                    min = if (min > 0) min else null,
                    max = if (max > 0) max else null,
                    message = message.ifEmpty {
                        StringBuilder("$name 长度不正确")
                            .apply { if (min > -1) append("，长度应该大于 $min") }
                            .apply { if (max > -1) append("，长度应该小于 $max") }
                            .toString()
                    }
                )
            )
        }
        annotation<FetchValidate> {
            validates.add(
                EditValidateEntry(
                    fetch = value,
                    message = message.ifEmpty { "$name 内容不合法" }
                )
            )
        }
        annotation<FunctionValidate> {
            validates.add(
                EditValidateEntry(
                    eval = value,
                    message = message.ifEmpty { "$name 内容不合法" }
                )
            )
        }
        return EditInfo(
            annotation<EditPropertyNode>()?.value ?: AkoDefaultNode.fieldEditProperty,
            annotation<EditInputNode>()?.value ?: AkoDefaultNode.fieldEditInput,
            required,
            allowEmpty,
            !hasAnnotation<Disabled>(),
            name,
            validates
        )
    }

    return CustomDbField(
        id,
        name,
        annotation<Description>()?.value,
        typeProvider,
        type,
        option,
        if (searchIgnore) null else SearchInfo(
            annotation<SearchPropertyNode>()?.value ?: AkoDefaultNode.fieldSearchProperty,
            searchEntries
        ),
        editInfo(),
        if (tableIgnore) null else ColumnInfo(
            annotation<TableColumnNode>()?.value ?: AkoDefaultNode.fieldTableColumn,
            tableWidth,
            annotation<ColumnIndex>()?.value,
        ),
    )

//    annotation<Upload> {
//        searchIgnore = true
//
//        content = "$url|$prefix"
//
//        type = ValueType.Type.UPLOAD.type
//        subtype = when (this.type) {
//            "image" -> ValueType.Type.UPLOAD_IMAGE.subtype
//            else -> ValueType.Type.UPLOAD.subtype
//        }
//    }
//
//    annotation<ValueType> {
//        type = value.type
//        subtype = value.subtype
//    }
//
//    when (type) {
//        ValueType.Type.DATE.type -> {
//            searchEntries.add(
//                SearchEntry(
//                    "default-entity-search-column-node",
//                    "gte",
//                    "开始时间",
//                    searchWidth
//                )
//            )
//            searchEntries.add(
//                SearchEntry(
//                    "default-entity-search-column-node",
//                    "lte",
//                    "结束时间",
//                    searchWidth
//                )
//            )
//        }
//
//        else -> searchEntries.add(
//            SearchEntry(
//                "default-entity-search-column-node",
//                "eq",
//                name,
//                searchWidth
//            )
//        )
//    }
//
//    annotation<Mapping> {
//        type = ValueType.Type.MAPPING.type
//        subtype = ValueType.Type.MAPPING.subtype
//
//        content = "${value.java.simpleName}|$field|$display"
//    }
//    annotation<EnumMapping> {
//        type = ValueType.Type.ENUM_MAPPING.type
//        subtype = ValueType.Type.ENUM_MAPPING.subtype
//
//        content = field
//        enum = mappings.mapIndexed { index, mapping ->
//            val i = if (mapping.index < 0) index else mapping.index
//            "$i:${mapping.value.java.simpleName}|${mapping.field}|${mapping.display}"
//        }
//    }
//    annotation<DbEnum> {
//        type = ValueType.Type.ENUM.type
//        subtype = ValueType.Type.ENUM.subtype
//
//        enum = value.toList()
//    }
//    annotation<DbFlag> {
//        type = ValueType.Type.ENUM.type
//        subtype = ValueType.Type.ENUM.subtype
//
//        enum = value.toList()
//    }


}

val Field.dbField: DbField
    get() = readFieldInfo(
        this@dbField.declaringClass.simpleName,
        name,
        type,
        this.kotlinProperty?.returnType?.isMarkedNullable ?: true
    ).let {
        DbField(
            this,
            it.id,
            it.name,
            it.description,

            it.search,
            it.edit,
            it.column,

            it.type,
            it.options,
            it.provider
        )
    }

