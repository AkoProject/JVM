package ako.protocol.type

fun interface AkoDefaultTypeProvider {
    operator fun invoke(fieldType: Class<*>): AkoTypeProvider<*, *, *>?
}