package ako.protocol.edit

data class CustomEditField(
    override val id: String,
    override val name: String,
    override val description: String?,
    override val type: Int,
    override val subtype: Int,
    override val content: String?,
    override val enum: List<String>?,
    override val edit: EditInfo?,
) : EditField