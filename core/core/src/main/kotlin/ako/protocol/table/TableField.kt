package ako.protocol.table

import ako.protocol.base.BaseField

interface TableField : BaseField {

    val column: ColumnInfo?

}