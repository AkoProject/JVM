package ako.model

data class EditValidateEntry(
    val require: Boolean? = null,
    val min: Int? = null,
    val max: Int? = null,
    val regexp: String? = null,
    val eval: String? = null,
    val fetch: String? = null,
    val message: String,
)