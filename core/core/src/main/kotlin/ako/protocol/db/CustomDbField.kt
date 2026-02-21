package ako.protocol.db

import ako.protocol.base.BaseField
import ako.protocol.edit.EditField
import ako.protocol.edit.EditInfo
import ako.protocol.search.SearchField
import ako.protocol.search.SearchInfo
import ako.protocol.table.ColumnInfo
import ako.protocol.table.TableField
import ako.protocol.type.AkoTypeProvider

data class CustomDbField(
    override val id: String,
    override val name: String,
    override val description: String?,
    val provider: AkoTypeProvider<*,*,*>?,
    override val type: String,
    override val options: Any?,
    override val search: SearchInfo?,
    override val edit: EditInfo?,
    override val column: ColumnInfo?,
) : BaseField, SearchField, EditField, TableField