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

    val fields: List<DbField> = ArrayList<DbField>().apply {
        add(type.allField.find { it.name == "id" }!!.dbField)
        type.allField
            .asSequence()
            .filter { it.name != "id" }
            .filter { !it.isStatic }
            .filter { !it.hasAnnotation<NoAkoField>() }
            .forEach { add(it.dbField) }
    }

}