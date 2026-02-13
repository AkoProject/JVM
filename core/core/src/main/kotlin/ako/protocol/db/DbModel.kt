package ako.protocol.db

import ako.annotation.EnumMapping
import ako.annotation.Mapping
import ako.annotation.NoAkoField
import ako.`fun`.*
import ako.model.protocol.button.ButtonEntry
import ako.model.base.AkoModel
import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnore
import java.lang.reflect.Field


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
        java.util.ArrayList.add(type.allField.find { it.name == "id" }!!.let { it.dbField.apply { checkField(it) } })
        type.allField
            .asSequence()
            .filter { it.name != "id" }
            .filter { !it.isStatic }
            .filter { !it.hasAnnotation<NoAkoField>() }
            .forEach { field ->
                field.dbField.apply {
                    checkField(field)
                    add(this)
                }
            }
    }

    fun DbField.checkField(field: Field){
        field.annotation<Mapping> {
            mappings[value.java.simpleName] = MappingEntry(value.java.simpleName, value.java, this.field) {
                field.isAccessible = true
                field.get(it) as? Int
            }
        }
        field.annotation<EnumMapping> {
            val mappingField = field.declaringClass.getDeclaredField(this.field)
            mappings.forEachIndexed { index, mapping ->
                val i = if (mapping.index < 0) index else mapping.index
                this@DbModel.mappings[mapping.value.java.simpleName] =
                    MappingEntry(mapping.value.java.simpleName, mapping.value.java, this.field) {
                        mappingField.isAccessible = true
                        val enum = mappingField.get(it) as Int
                        if (enum != i) null
                        else {
                            field.isAccessible = true
                            field.get(it)
                        }
                    }
            }
        }
    }
}