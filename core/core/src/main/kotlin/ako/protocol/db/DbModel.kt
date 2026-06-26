package ako.protocol.db

import ako.annotation.NoAkoField
import ako.`fun`.*
import ako.model.base.AkoModel
import ako.protocol.button.ButtonEntry
import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnore

data class DbModel<T : AkoModel>(
    @field:JsonIgnore
    @field:JSONField(serialize = false)
    val type: Class<T>,
    val id: String,
    val name: String,
    val previous: String?,
    val permission: String?,
    val index: Int,
    val displayAble: Boolean,
    val pageNode: String,
    val searchNode: String,
    val tableNode: String,
    val editNode: String,
    val iconNode: String,
    val modelButtons: List<ButtonEntry>,
    val operateButtons: List<ButtonEntry>
) {

    data class MappingEntry(
        val name: String,
        val model: Class<out AkoModel>,
        val fieldName: String = "id",
        val field: (Any) -> Any?
    )

    @field:JsonIgnore
    @field:JSONField(serialize = false)
    val mappings = HashMap<String, MappingEntry>()

    val idField: DbField
    val fields: List<DbField>

    companion object{
        private val jpaId = runCatching { Class.forName("jakarta.persistence.Id") }.getOrNull() as? Class<out Annotation>
    }

    init {
        val allField = type.allField
        val idField = allField.find { if (jpaId != null) it.getAnnotation(jpaId) != null else it.name == "id" }
            ?: error("模型 $id 没有 id 字段！")
        this.idField = idField.dbField
        val fields = ArrayList<DbField>()
        fields.add(this.idField)
        type.allField
            .asSequence()
            .filter { it != idField }
            .filter { !it.isStatic }
            .filter { !it.hasAnnotation<NoAkoField>() }
            .forEach { fields.add(it.dbField) }
        this.fields = fields
    }

}