package ako.protocol.edit

data class CustomEditField(
    override val id: String,
    override val name: String,
    override val description: String?,
    override val type: String,
    override val options: Any?,
    override val edit: EditInfo?,
) : EditField