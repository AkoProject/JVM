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
import java.lang.reflect.Field
import kotlin.reflect.jvm.kotlinProperty

val <T : AkoModel> Class<T>.dbModel: ako.protocol.db.DbModel<T>
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
            annotation<ModelNode>()?.pageNode ?: "default-entity-page-node",
            annotation<ModelNode>()?.searchNode ?: "default-entity-search-node",
            annotation<ModelNode>()?.tableNode ?: "default-entity-table-node",
            annotation<ModelNode>()?.editNode ?: "default-entity-edit-node",
            annotation<ModelNode>()?.iconNode ?: "default-entity-icon-node",
            modelButtons,
            operateButtons
        )
    }

val Field.dbField: DbField
    get() {
        val id = name
        val name = annotation<DbName>()?.value ?: name

        var type = 0
        var subtype = 0

        var searchIgnore = hasAnnotation<SearchIgnore>()
        var tableIgnore = hasAnnotation<TableIgnore>()
        var editIgnore = hasAnnotation<EditIgnore>()

        var content: String? = null
        var enum: List<String>? = null
        var mappingEnum: List<String>? = null

        annotation<Upload> {
            searchIgnore = true

            content = "$url|$prefix"

            type = ValueType.Type.UPLOAD.type
            subtype = when (this.type) {
                "image" -> ValueType.Type.UPLOAD_IMAGE.subtype
                else -> ValueType.Type.UPLOAD.subtype
            }
        }

        val searchWidth = annotation<SearchColumnWidth>()?.value ?: "200px"
        val tableWidth = annotation<TableColumnWidth>()?.value ?: 200


        annotation<ValueType> {
            type = value.type
            subtype = value.subtype
        }

        var searchEntries: MutableList<SearchEntry> = ArrayList()

        when (type) {
            ValueType.Type.DATE.type -> {
                searchEntries.add(
                    SearchEntry(
                        "default-entity-search-column-node",
                        "gte",
                        "开始时间",
                        searchWidth
                    )
                )
                searchEntries.add(
                    SearchEntry(
                        "default-entity-search-column-node",
                        "lte",
                        "结束时间",
                        searchWidth
                    )
                )
            }

            else -> searchEntries.add(
                SearchEntry(
                    "default-entity-search-column-node",
                    "eq",
                    name,
                    searchWidth
                )
            )
        }

        annotation<SearchType> {
            searchEntries = value.map {
                SearchEntry(
                    "default-entity-search-column-node",
                    it.opt,
                    name,
                    searchWidth
                )
            }.toMutableList()
        }

        annotation<Mapping> {
            type = ValueType.Type.MAPPING.type
            subtype = ValueType.Type.MAPPING.subtype

            content = "${value.java.simpleName}|$field|$display"
        }
        annotation<EnumMapping> {
            type = ValueType.Type.ENUM_MAPPING.type
            subtype = ValueType.Type.ENUM_MAPPING.subtype

            content = field
            enum = mappings.mapIndexed { index, mapping ->
                val i = if (mapping.index < 0) index else mapping.index
                "$i:${mapping.value.java.simpleName}|${mapping.field}|${mapping.display}"
            }
        }
        annotation<DbEnum> {
            type = ValueType.Type.ENUM.type
            subtype = ValueType.Type.ENUM.subtype

            enum = value.toList()
        }
        annotation<DbFlag> {
            type = ValueType.Type.ENUM.type
            subtype = ValueType.Type.ENUM.subtype

            enum = value.toList()
        }

        fun editInfo(): EditInfo? {
            if (editIgnore) return null
            val validates = ArrayList<EditValidateEntry>()
            var required = false
            annotation<Required> {
                required = true
                validates.add(EditValidateEntry(require = true, message = value))
            } ?: this.kotlinProperty?.returnType?.isMarkedNullable?.let {
                if (!it) {
                    required = true
                    validates.add(EditValidateEntry(require = true, message = "$name 不能为空"))
                }
            }
            annotation<RegExpValidate> {
                validates.add(EditValidateEntry(regexp = value, message = message.ifEmpty { "$name 内容不合法" }))
            }
            annotation<RangeValidate> {
                if (min == -1 && max == -1) return@annotation
                if (min < max) error("字段 ${this@dbField.declaringClass.name}.${this@dbField.name} 的 Range 声明不正确，$min 不能大于 $max！")
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
                annotation<FieldNode>()?.editColumnNode ?: "default-entity-edit-column-node",
                required,
                !hasAnnotation<Disabled>(),
                name,
                validates
            )
        }

        return DbField(
            this,
            id,
            name,
            annotation<Description>()?.value,

            if (searchIgnore) null else SearchInfo(
                annotation<FieldNode>()?.searchColumnNode ?: "default-entity-search-property-node",
                searchEntries
            ),
            editInfo(),
            if (tableIgnore) null else ColumnInfo(
                annotation<FieldNode>()?.tableColumnNode ?: "default-entity-table-column-node",
                tableWidth,
                annotation<ColumnIndex>()?.value,
            ),
            type,
            subtype,
            content,
            enum
        )
    }
