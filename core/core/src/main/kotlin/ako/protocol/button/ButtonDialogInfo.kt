package ako.protocol.button

data class ButtonDialogInfo(
    val component: String,
    val title: String? = null,
    val style: String? = null,
    val close: Boolean? = null,
    val needSingle: Boolean? = null,
    val needMulti: Boolean? = null
)