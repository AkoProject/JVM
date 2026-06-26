package ako.protocol.button

import ako.protocol.edit.CustomEditField

data class ButtonPanelInfo(
    val id: String,
    val name: String,
    val url: String?,
    val method: String?,
    val data: String?,
    val editNode: String?,
    val fields: List<CustomEditField>,
)