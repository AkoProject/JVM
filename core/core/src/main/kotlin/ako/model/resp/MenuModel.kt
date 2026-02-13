package ako.model.resp

import ako.protocol.db.DbModel

data class MenuModel(
    val id: String,
    val name: String,
    val icon: String?,
    val previous: String?,
    val permission: String?,
    val index: Int,
    val node: String?,
    val dbType: DbModel<*>?
)