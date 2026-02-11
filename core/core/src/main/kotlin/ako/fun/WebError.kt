package ako.`fun`

class WebError(val code: Int, override val message: String) : RuntimeException(message, null, false, false)

fun webError(code: Int, message: String): Nothing = throw WebError(code, message)
