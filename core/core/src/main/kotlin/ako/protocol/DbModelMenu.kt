package ako.protocol

data class DbModelMenu(
    var channel: String = "",
    var identifier: String = "",
    var name: String = "",
    var previous: Int? = null,
    var iconNode: String? = null,
    var pageNode: String? = null,
    var permission: String? = null,
    var index: Int = 0
)