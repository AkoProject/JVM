package ako.model

data class EditInfo(
    val component: String,
    val require: Boolean,
    val editable: Boolean,
    val placeholder: String,
    val validate: List<EditValidateEntry>,
)