package ako.spring.data

import ako.model.base.AkoModel
import ako.model.base.SoftDeleteModel
import ako.model.resp.PageResp
import ako.protocol.db.DbField
import ako.protocol.db.DbModel
import ako.spring.access.AkoAccess
import ako.spring.db.AkoSpringDatabase
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.context.ApplicationContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType
import java.util.Locale

/**
 * JPA implementation of [AkoSpringDatabase].
 *
 * Repository discovery is intentionally based on public Spring Data
 * contracts. Dynamic Ako filters use the Jakarta Criteria API, so the same
 * implementation works with Hibernate 6/7 and Spring Data JPA 3.x/4.x.
 */
open class AkoSpringDataDatabase(
    private val applicationContext: ApplicationContext,
    private val entityManager: EntityManager,
    transactionManager: PlatformTransactionManager,
) : AkoSpringDatabase {

    private val transactionTemplate = TransactionTemplate(transactionManager)

    private val repositoriesByModel: Map<Class<out AkoModel>, JpaRepository<Any, Any>> by lazy {
        discoverRepositories()
    }

    override val modelTypes: Collection<Class<out AkoModel>>
        get() = repositoriesByModel.keys

    override fun <T : AkoModel> whereList(
        model: DbModel<out T>,
        params: Map<String, Any>?,
        sort: Map<String, String>?,
        page: Int?,
        size: Int?,
    ): List<T> = inTransaction {
        val query = createQuery(model, params, sort)
        if (page != null && size != null) {
            query.firstResult = page.coerceAtLeast(0) * size.coerceAtLeast(1)
            query.maxResults = size.coerceAtLeast(1)
        }

        @Suppress("UNCHECKED_CAST")
        query.resultList as List<T>
    }

    override fun <T : AkoModel> wherePage(
        model: DbModel<out T>,
        params: Map<String, Any>?,
        sort: Map<String, String>?,
        page: Int,
        size: Int,
    ): PageResp<T> = inTransaction {
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceAtLeast(1)
        val dataQuery = createQuery(model, params, sort)
        dataQuery.firstResult = safePage * safeSize
        dataQuery.maxResults = safeSize

        val countQuery = createCountQuery(model, params)

        @Suppress("UNCHECKED_CAST")
        val data = dataQuery.resultList as List<T>
        PageResp(safePage, safeSize, (countQuery.singleResult as Number).toInt(), data)
    }

    override fun <T : AkoModel> save(data: T): T = inTransaction {
        val modelType = repositoriesByModel.keys.firstOrNull { it.isAssignableFrom(data.javaClass) }
            ?: error("没有找到模型 ${data.javaClass.name} 对应的 Spring Data Repository")
        val repository = repositoriesByModel.getValue(modelType)

        @Suppress("UNCHECKED_CAST")
        repository.save(data) as T
    }

    override fun delete(model: DbModel<out AkoModel>, id: Any?) {
        requireNotNull(id) { "模型 ${model.id} 的 id 不能为空" }
        inTransaction {
            val repository = repositoriesByModel[model.type]
                ?: error("没有找到模型 ${model.type.name} 对应的 Spring Data Repository")

            if (SoftDeleteModel::class.java.isAssignableFrom(model.type)) {
                val entity = repository.findById(id).orElse(null)
                if (entity is SoftDeleteModel) {
                    entity.deleteTime = System.currentTimeMillis()
                    repository.save(entity)
                }
            } else {
                repository.deleteById(id)
            }
        }
    }

    private fun <T : AkoModel> createQuery(
        model: DbModel<out T>,
        params: Map<String, Any>?,
        sort: Map<String, String>?,
    ): jakarta.persistence.TypedQuery<T> {
        @Suppress("UNCHECKED_CAST")
        val entityType = model.type as Class<T>
        val criteria = entityManager.criteriaBuilder.createQuery(entityType)
        val root = criteria.from(entityType)
        val predicates = predicates(entityManager.criteriaBuilder, root, model, params)
        if (predicates.isNotEmpty()) criteria.where(*predicates.toTypedArray())
        criteria.orderBy(*orders(entityManager.criteriaBuilder, root, model, sort).toTypedArray())
        return entityManager.createQuery(criteria)
    }

    private fun <T : AkoModel> createCountQuery(
        model: DbModel<out T>,
        params: Map<String, Any>?,
    ): jakarta.persistence.TypedQuery<Long> {
        @Suppress("UNCHECKED_CAST")
        val entityType = model.type as Class<T>
        val criteria: CriteriaQuery<Long> = entityManager.criteriaBuilder.createQuery(Long::class.javaObjectType)
        val root = criteria.from(entityType)
        val builder = entityManager.criteriaBuilder
        val predicates = predicates(builder, root, model, params)
        criteria.select(builder.count(root))
        if (predicates.isNotEmpty()) criteria.where(*predicates.toTypedArray())
        return entityManager.createQuery(criteria)
    }

    private fun <T : AkoModel> predicates(
        builder: CriteriaBuilder,
        root: Root<T>,
        model: DbModel<out T>,
        params: Map<String, Any>?,
    ): List<Predicate> {
        val result = ArrayList<Predicate>()

        if (SoftDeleteModel::class.java.isAssignableFrom(model.type)) {
            result += builder.equal(root.get<Any>("deleteTime"), 0L)
        }

        params.orEmpty().forEach { (key, rawValue) ->
            val condition = parseCondition(key)
            val field = model.fields.firstOrNull { it.id == condition.field }
                ?: error("模型 ${model.id} 中不存在可查询字段 ${condition.field}")
            val path = root.get<Any>(field.id)

            if (condition.operator == "isNull") {
                result += builder.isNull(path)
                return@forEach
            }

            val value = if (condition.operator == "in") {
                val values = valuesOf(rawValue).mapNotNull { convert(field, it) }
                path.`in`(values)
            } else {
                val converted = convert(field, rawValue)
                when (condition.operator) {
                    "eq" -> if (converted == null) builder.isNull(path) else builder.equal(path, converted)
                    "ne" -> if (converted == null) builder.isNotNull(path) else builder.notEqual(path, converted)
                    "like" -> builder.like(path.`as`(String::class.java), converted?.toString() ?: "")
                    "gt" -> comparable(builder, path, converted, field, "gt")
                    "lt" -> comparable(builder, path, converted, field, "lt")
                    "gte" -> comparable(builder, path, converted, field, "gte")
                    "lte" -> comparable(builder, path, converted, field, "lte")
                    else -> error("不支持的操作符：${condition.operator}")
                }
            }
            result += value
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    private fun comparable(
        builder: CriteriaBuilder,
        path: Expression<Any>,
        value: Any?,
        field: DbField,
        operator: String,
    ): Predicate {
        require(value is Comparable<*>) {
            "字段 ${field.id} 不支持 $operator 查询"
        }
        val expression = path as Expression<Comparable<Any>>
        val comparable = value as Comparable<Any>
        return when (operator) {
            "gt" -> builder.greaterThan(expression, comparable)
            "lt" -> builder.lessThan(expression, comparable)
            "gte" -> builder.greaterThanOrEqualTo(expression, comparable)
            "lte" -> builder.lessThanOrEqualTo(expression, comparable)
            else -> error("不支持的比较操作符：$operator")
        }
    }

    private fun <T : AkoModel> orders(
        builder: CriteriaBuilder,
        root: Root<T>,
        model: DbModel<out T>,
        sort: Map<String, String>?,
    ): List<jakarta.persistence.criteria.Order> {
        val entries: List<Pair<String, String>> = sort?.entries
            ?.map { it.key to it.value }
            ?.takeIf { it.isNotEmpty() }
            ?: listOf(model.idField.id to "DESC")

        return entries.map { (fieldId, direction) ->
            val field = model.fields.firstOrNull { it.id == fieldId }
                ?: error("模型 ${model.id} 中不存在可排序字段 $fieldId")
            when (direction.uppercase(Locale.ROOT)) {
                "ASC" -> builder.asc(root.get<Any>(field.id))
                "DESC" -> builder.desc(root.get<Any>(field.id))
                else -> error("排序方向必须是 ASC 或 DESC：$direction")
            }
        }
    }

    private data class Condition(val field: String, val operator: String)

    private fun parseCondition(key: String): Condition {
        val separator = key.lastIndexOf('_')
        if (separator < 0) return Condition(key, "eq")
        val field = key.substring(0, separator)
        val operator = key.substring(separator + 1)
        return if (operator in OPERATORS && field.isNotEmpty()) Condition(field, operator)
        else Condition(key, "eq")
    }

    private fun valuesOf(value: Any?): List<Any?> = when (value) {
        null -> emptyList()
        is Collection<*> -> value.toList()
        is Array<*> -> value.toList()
        is String -> value.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        else -> listOf(value)
    }

    private fun convert(field: DbField, value: Any?): Any? {
        if (value == null) return null
        if (field.field.type.isEnum) {
            if (field.field.type.isInstance(value)) return value
            val constants = field.field.type.enumConstants
            val text = value.toString()
            return constants.firstOrNull {
                (it as Enum<*>).name == text || it.toString() == text
            } ?: text.toIntOrNull()?.let { constants.getOrNull(it) }
                ?: error("无法将 $value 转换为枚举 ${field.field.type.name}")
        }
        return field.convert(value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <R> inTransaction(block: () -> R): R =
        transactionTemplate.execute { block() } as R

    @Suppress("UNCHECKED_CAST")
    private fun discoverRepositories(): Map<Class<out AkoModel>, JpaRepository<Any, Any>> {
        val result = LinkedHashMap<Class<out AkoModel>, JpaRepository<Any, Any>>()
        applicationContext.getBeansOfType(JpaRepository::class.java).forEach { (beanName, rawRepository) ->
            val repositoryType = repositoryType(beanName, rawRepository) ?: return@forEach
            val modelType = findModelType(repositoryType) ?: return@forEach
            val repository = rawRepository as JpaRepository<Any, Any>
            check(result.put(modelType, repository) == null) {
                "模型 ${modelType.name} 对应了多个 Spring Data Repository"
            }
        }
        return result
    }

    private fun repositoryType(beanName: String, bean: Any): Class<*>? {
        val candidates = sequenceOf(applicationContext.getType(beanName)) +
            bean.javaClass.interfaces.asSequence()
        return candidates.filterNotNull().firstOrNull {
            it.isInterface && JpaRepository::class.java.isAssignableFrom(it)
        }
    }

    private fun findModelType(type: Type, variables: Map<TypeVariable<*>, Type> = emptyMap()): Class<out AkoModel>? {
        when (type) {
            is ParameterizedType -> {
                val raw = type.rawType as? Class<*> ?: return null
                val actualArguments = type.actualTypeArguments.map { resolve(it, variables) }
                val nextVariables = LinkedHashMap(variables)
                raw.typeParameters.forEachIndexed { index, variable ->
                    actualArguments.getOrNull(index)?.let { nextVariables[variable] = it }
                }

                if (raw == AkoAccess::class.java || raw == JpaRepository::class.java) {
                    return asAkoModel(actualArguments.firstOrNull())
                }

                raw.genericInterfaces.forEach { findModelType(it, nextVariables)?.let { model -> return model } }
                raw.genericSuperclass?.let { findModelType(it, nextVariables)?.let { model -> return model } }
            }

            is Class<*> -> {
                if (type == AkoAccess::class.java || type == JpaRepository::class.java) return null
                type.genericInterfaces.forEach { findModelType(it, variables)?.let { model -> return model } }
                type.genericSuperclass?.let { findModelType(it, variables)?.let { model -> return model } }
            }
        }
        return null
    }

    private fun resolve(type: Type, variables: Map<TypeVariable<*>, Type>): Type = when (type) {
        is TypeVariable<*> -> variables[type] ?: type
        is WildcardType -> type.upperBounds.firstOrNull()?.let { resolve(it, variables) } ?: type
        else -> type
    }

    @Suppress("UNCHECKED_CAST")
    private fun asAkoModel(type: Type?): Class<out AkoModel>? {
        val clazz = type as? Class<*> ?: return null
        return clazz.takeIf { AkoModel::class.java.isAssignableFrom(it) } as? Class<out AkoModel>
    }

    companion object {
        private val OPERATORS = setOf("eq", "gt", "lt", "gte", "lte", "ne", "like", "in", "isNull")
    }
}
