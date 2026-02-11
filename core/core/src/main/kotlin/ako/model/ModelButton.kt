package ako.model

data class ModelButton(
    val name: String = "",
    val index: Int = 0,
    val url: String = "",
    val method: String = "GET",
    val newWindow: Boolean = false,
    val eval: String? = null
)