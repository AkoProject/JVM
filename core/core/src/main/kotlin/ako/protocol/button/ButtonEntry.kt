package ako.protocol.button

data class ButtonEntry(
    val name: String = "",
    val index: Int = 0,
    val url: String = "",
    val method: String = "GET",
    val eval: String? = null,
    val reconfirm: String? = null,
    val type: String,
    val component: String? = null,
    val edit: ButtonEditInfo? = null,
    val dialog: ButtonDialogInfo? = null,
)