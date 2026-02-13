package ako.protocol.base

interface BaseField {
    val id: String
    val name: String
    val description: String?

    val type: Int
    val subtype: Int

    val content: String?
    val enum: List<String>?
}