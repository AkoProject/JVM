package ako.protocol.edit

data class EditInfo(
    val propertyComponent: String,
    val inputComponent: String,
    val require: Boolean,
    val allowEmpty: Boolean,
    val editable: Boolean,
    val placeholder: String,
    val validate: List<EditValidateEntry>,
)