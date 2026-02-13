package ako.annotation

import ako.model.base.AkoModel
import kotlin.reflect.KClass

/*** 上传内容字段
 * @param url 上传地址
 * @param prefix 访问前缀
 */
@Target(AnnotationTarget.FIELD)
annotation class Upload(val url: String, val prefix: String, val type: String)

/*** 搜索方式
 * @param value 搜索类型
 */
@Target(AnnotationTarget.FIELD)
annotation class SearchType(vararg val value: Type) {
    enum class Type(val opt: String) {
        // 等于查询（默认）
        Equal("eq"),

        // 前后模糊查询
        Like("like"),

        // 大于查询
        Greater("gt"),

        // 小于查询
        Less("lt"),

        // 大于等于查询
        GreaterEqual("gte"),

        // 小于等于查询
        LessEqual("lte"),
    }
}

/*** 值类型
 * @param value 值类型
 */
@Target(AnnotationTarget.FIELD)
annotation class ValueType(val value: Type) {
    enum class Type(val type: Int, val subtype: Int) {
        // 文本类型
        TEXT(0, 0),

        // 文本域类型
        TEXTAREA(1, 0),

        // 日期类型，日期与时间类型默认会创建一个前后关联的查询。
        DATE(40, 0),

        // 时间类型，日期与时间类型默认会创建一个前后关联的查询。
        TIME(40, 1),

        // 日期时间类型，日期与时间类型默认会创建一个前后关联的查询。
        DATETIME(40, 2),

        // 关联映射类型，请勿手动指定该类型
        MAPPING(50, 0),

        // 枚举关联类型，请勿手动指定该类型
        ENUM_MAPPING(50, 1),

        // 枚举类型，请勿手动指定该类型
        ENUM(100, 0),

        // 上传文件类型
        UPLOAD(120, 0),

        // 上传图片类型
        UPLOAD_IMAGE(120, 1),
    }
}

/*** 枚举类型
 * @param value 枚举值
 *
 * 默认会按下标对应枚举值中配置的名字，如果想指定下标和名字，请使用 "下标:名字" 的格式。
 */
@Target(AnnotationTarget.FIELD)
annotation class DbEnum(vararg val value: String, val index: Int = -1)

/*** 关联枚举类型
 * @param field 关联枚举字段
 * @param value 关联枚举值，如果 DbEnum 的 index 保持 -1，则会按数组下标自动对应。
 */
@Target(AnnotationTarget.FIELD)
annotation class EnumEnum(val field: String, vararg val value: DbEnum)

/*** 标记字段
 * @param value 标记值
 *
 * 该注解类似于 DbEnum，但不会通过下标映射值，而是直接通过字符串值进行存储与读取。
 * 允许通过 "值" 或 "值:显示名" 的格式进行配置。
 */
@Target(AnnotationTarget.FIELD)
annotation class DbFlag(vararg val value: String)

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
