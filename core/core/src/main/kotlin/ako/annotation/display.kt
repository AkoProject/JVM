package ako.annotation

/*** 前端映射模型配置
 * @param pageNode 页面组件
 * @param searchNode 搜索组件
 * @param tableNode 列表组件
 * @param editNode 编辑组件
 *
 * 如果指定了一个值，前端会通过这个值在 Vue 内按名称寻找组件，请确认组件名称正确，且已经注册到 Vue App。
 */
annotation class ModelNode(
    val pageNode: String = "default-entity-page-node",
    val searchNode: String = "default-entity-search-node",
    val tableNode: String = "default-entity-table-node",
    val editNode: String = "default-entity-edit-node",
    val iconNode: String = "default-entity-icon-node",
)

/*** 前端映射字段配置
 * @param searchColumnNode 搜索列组件
 * @param tableColumnNode 列表列组件
 * @param editColumnNode 编辑列组件
 *
 * 如果指定了一个值，前端会通过这个值在 Vue 内按名称寻找组件，请确认组件名称正确，且已经注册到 Vue App。
 */
@Target(AnnotationTarget.FIELD)
annotation class FieldNode(
    val searchColumnNode : String = "default-entity-search-property-node",
    val tableColumnNode: String = "default-entity-table-column-node",
    val editColumnNode: String = "default-entity-edit-column-node",
)

// 标记该字段不在查询组件中展示
@Target(AnnotationTarget.FIELD)
annotation class SearchIgnore

// 标记该字段不在列表组件中展示
@Target(AnnotationTarget.FIELD)
annotation class TableIgnore

// 标记该字段不在编辑组件中展示
@Target(AnnotationTarget.FIELD)
annotation class EditIgnore

// 标记字段描述，前端会在编辑组件中展示这个面数
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
annotation class Description(val value: String)

/** 不可编辑字段，前端会在编辑组件中使这个字段不可编辑
 * @param value 当前字段值不合法时，提示信息
 */
@Target(AnnotationTarget.FIELD)
annotation class Disabled(val value: String = "")

/** 必填字段，前端会在编辑组件中使这个字段必填
 * @param value 当前字段值不合法时，提示信息
 */
@Target(AnnotationTarget.FIELD)
annotation class Required(val value: String = "")

/** 范围限制字段，前端会在编辑组件中使这个字段值在指定范围内
 * @param min 最小值，默认为 -1，表示不限制
 * @param max 最大值，默认为 -1，表示不限制
 * @param message 当前字段值不合法时，提示信息
 *
 * 当 参数类型 为 String 时，则验证内容长度。
 * 当 参数类型 为 Number 时，则验证数值范围。
 */
@Target(AnnotationTarget.FIELD)
annotation class RangeValidate(
    val min: Int = -1,
    val max: Int = -1,
    val message: String = ""
)

/** 使用正则表达式验证字段
 * @param value 正则表达式
 * @param message 当前字段值不合法时，提示信息
 */
@Target(AnnotationTarget.FIELD)
annotation class RegExpValidate(
    val value: String,
    val message: String = ""
)
/** 字段验证函数
 * @param value JavaScript 函数值
 * @param message 当前字段值不合法时，提示信息
 *
 * 该类型验证只在前端生效，不在后端生效！
 * 函数有四个参数，其类型为 async (value: string, data: Model, model: DbModel, field: DbField) => Promise<void>。
 */
@Target(AnnotationTarget.FIELD)
annotation class FunctionValidate(
    val value: String,
    val message: String = ""
)

/** 远程字段验证
 * @param value 目标地址
 * @param message 当前字段值不合法时，提示信息
 *
 * 该类型验证只在前端生效，不在后端生效！
 * 会向目标地址发送一个 POST 请求，Content-Type 为 application/json。
 * 值类型为 {value: String, data: Model, model: DbModel, field: DbField}。
 */
@Target(AnnotationTarget.FIELD)
annotation class FetchValidate(
    val value: String,
    val message: String = ""
)

// 标记字段在搜索组件中展示的宽度
@Target(AnnotationTarget.FIELD)
annotation class SearchColumnWidth(val value: String)

// 标记字段在列表组件中展示的宽度
@Target(AnnotationTarget.FIELD)
annotation class TableColumnWidth(val value: Int)