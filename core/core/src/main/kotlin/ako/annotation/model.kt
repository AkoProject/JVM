package ako.annotation

import ako.annotation.ButtonPanel.Companion.toButtonPanelInfo
import ako.annotation.PanelField.Companion.toCustomEditField
import ako.`fun`.AkoAnnotationContainer
import ako.`fun`.notEmptyOrNull
import ako.`fun`.readFieldInfo
import ako.model.base.AkoModel
import ako.protocol.button.ButtonDialogInfo
import ako.protocol.button.ButtonEntry
import ako.protocol.button.ButtonPanelInfo
import ako.protocol.edit.CustomEditField
import kotlin.text.ifEmpty


/*** 数据库元素名
 * @param value 元素名
 *
 * 当标记在一个 Model 上，表示这个 Model 对应的展示表名。
 * 当标记在一个 Field 上，表示这个 Field 对应的展示列名。
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
annotation class DbName(val value: String, val index: Int = 100)

/*** 非 Ako 字段
 * 当一个 Field 标记这个注解，Ako 不会处理这个字段。
 */
@Target(AnnotationTarget.FIELD)
annotation class NoAkoField

/*** 非 Ako 模型
 * 当一个 模型 标记这个注解，Ako 不会处理这个模型。
 */
@Target(AnnotationTarget.CLASS)
annotation class NoAkoModel

/** 模型操作按钮
 * @param name 按钮名
 * @param url 按钮链接
 * @param method 请求方法
 * @param eval 按钮点击后执行的 JS 代码
 * @param reconfirm 二次确认提示内容
 * @param type 按钮颜色，对应 ElementPlus ElButton 组件的 type 属性
 * @param component 按钮组件名，若不为空，则使用该组件渲染按钮
 *
 * 若 eval 不为空，则会执行 eval 代码。
 * 若 eval 为空则会请求 url。
 * 在请求 URL 时默认使用 GET 请求，会以 $id 为表示替换为选项 id。
 * 若 method 为 popup，则会在新窗口打开，此时只能以 GET 发送请求。
 *
 * eval 块为函数块，提供三个参数
 * - single 为当前单选选中项，可能为空。
 * - multi 为当前多选选中项数组，不可为空。
 * - props 提供更多可选函数。
 */
@Repeatable
@Target(AnnotationTarget.CLASS)
annotation class ModelButton(
    val name: String = "",
    val index: Int = 0,
    val url: String = "",
    val method: String = "GET",
    val eval: String = "",
    val reconfirm: String = "",
    val type: String = "default",
    val component: String = "",
    val dialog: ButtonDialog = ButtonDialog(component = ""),
    val panel: ButtonPanel = ButtonPanel("", "")
) {
    companion object {
        fun ModelButton.toModelButton() =
            ButtonEntry(
                name,
                index,
                url,
                method,
                eval.ifEmpty { null },
                reconfirm.ifEmpty { null },
                type,
                component.ifEmpty { null },
                null,
                dialog.takeIf { it.component != "" }?.let {
                    ButtonDialogInfo(
                        it.component,
                        it.title.ifEmpty { null },
                        it.style.ifEmpty { null },
                        if (it.close) true else null,
                        if (it.needSingle) true else null,
                        if (it.needMulti) true else null
                    )
                },
                panel.takeIf { it.id.isNotEmpty() || it.name.isNotEmpty() || it.url.isNotEmpty() || it.method.isNotEmpty() }
                    ?.toButtonPanelInfo(),
            )
    }
}

/** 按钮弹窗配置
 * @param component 组件名，必填
 * @param title 弹窗标题
 * @param style 弹窗样式字符串
 * @param close 是否显示关闭按钮
 * @param needSingle 是否需要单选选中项
 * @param needMulti 是否需要多选选中项
 */
annotation class ButtonDialog(
    val component: String,
    val title: String = "",
    val style: String = "",
    val close: Boolean = false,
    val needSingle: Boolean = false,
    val needMulti: Boolean = false
)

annotation class ButtonPanel(
    val id: String = "",
    val name: String = "",
    val url: String = "",
    val method: String = "",
    vararg val fields: PanelField
) {
    companion object {
        fun ButtonPanel.toButtonPanelInfo() =
            ButtonPanelInfo(
                id,
                name,
                url.notEmptyOrNull(),
                method.notEmptyOrNull(),
                "default-entity-edit-node",
                fields.map { it.toCustomEditField(id) }
            )
    }
}


annotation class PanelField(
    val id: String,
    val nullable: Boolean = true,
    val allowEmpty: Boolean = false,
    val name: DbName = DbName(""),
    val description: Description = Description(""),
    val valueType: ValueType = ValueType(ValueType.Type.TEXT),
    val enum: DbEnum = DbEnum(),
    val enumEnum: EnumEnum = EnumEnum(""),
    val flag: DbFlag = DbFlag(),
    val mapping: Mapping = Mapping(AkoModel::class, display = ""),
    val enumMapping: EnumMapping = EnumMapping(""),
    // 该参数仅作为默认实例使用，如有需设置允许为 null，则需要将 emptyAble 设置为 true。
    val empty: AllowEmpty = AllowEmpty()
) {
    companion object {
        fun PanelField.toCustomEditField(model: String): CustomEditField {
            val annotations = ArrayList<Annotation>()
            if (name.value.isNotEmpty()) annotations.add(name)
            if (description.value.isNotEmpty()) annotations.add(description)
            if (valueType.value != ValueType.Type.TEXT) annotations.add(valueType)
            if (enum.value.isNotEmpty()) annotations.add(enum)
            if (enumEnum.value.isNotEmpty()) annotations.add(enumEnum)
            if (flag.value.isNotEmpty()) annotations.add(flag)
            if (mapping.value != AkoModel::class) annotations.add(mapping)
            if (enumMapping.field.isNotEmpty()) annotations.add(enumMapping)
            if (allowEmpty) annotations.add(empty)
            return AkoAnnotationContainer(annotations.toTypedArray())
                .readFieldInfo(model, id, String::class.java, nullable)
                .let {
                    CustomEditField(
                        it.id,
                        it.name,
                        it.description,
                        it.type,
                        it.options,
                        it.edit,
                    )
                }
        }
    }
}

/** 模型操作按钮
 * 如果提供该选项，则忽略所有直接提供的 ModelButton，使用该选项提供的 ModelButton。
 * 使用该选项可以覆盖默认提供的 查询、新增、批量删除 按钮。
 */
annotation class ModelButtons(
    vararg val value: ModelButton
)

/** 表格数据操作按钮
 * @param name 按钮名
 * @param url 按钮链接
 * @param method 请求方法
 * @param eval 按钮点击后执行的 JS 代码
 * @param reconfirm 二次确认提示内容
 * @param type 按钮颜色，对应 ElementPlus ElButton 组件的 type 属性
 * @param component 按钮组件名，若不为空，则使用该组件渲染按钮
 *
 * 若 eval 不为空，则会执行 eval 代码。
 * 若 eval 为空则会请求 url。
 * 在请求 URL 时默认使用 GET 请求，会以 $id 为表示替换为选项 id。
 * 若 method 为 popup，则会在新窗口打开，此时只能以 GET 发送请求。
 *
 * eval 块为函数块，提供二个参数
 * - entity 为当前单选选中项，可能为空。
 * - props 提供更多可选函数。
 */
@Repeatable
@Target(AnnotationTarget.CLASS)
annotation class OperateButton(
    val name: String = "",
    val index: Int = 0,
    val url: String = "",
    val method: String = "GET",
    val eval: String = "",
    val reconfirm: String = "",
    val type: String = "default",
    val component: String = ""
) {
    companion object {
        fun OperateButton.toModelButton() =
            _root_ide_package_.ako.protocol.button.ButtonEntry(
                name,
                index,
                url,
                method,
                eval.ifEmpty { null },
                reconfirm.ifEmpty { null },
                type,
                component.ifEmpty { null }
            )
    }
}

annotation class OperateButtons(
    vararg val value: OperateButton
)

@Target(AnnotationTarget.FIELD)
annotation class ColumnIndex(val value: Int = 100)

annotation class DefaultSort(val value: String)