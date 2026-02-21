package ako.protocol.base

interface BaseField {
    val id: String
    val name: String
    val description: String?

    val type: String
    val options: Any?
}