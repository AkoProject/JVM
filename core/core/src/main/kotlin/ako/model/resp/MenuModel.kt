package ako.model.resp

import ako.model.DbModel

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