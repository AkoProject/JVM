package ako.annotation

import ako.model.base.AkoModel
import ako.protocol.type.AkoTypeProvider
import kotlin.reflect.KClass

/*** 自定义值类型提供者
 * @param provider 提供者类，必须实现 AkoTypeProvider 接口。
 */
annotation class AkoType(val provider: KClass<out AkoTypeProvider<*, *, *>>)

annotation class Identifier(val value: String)

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