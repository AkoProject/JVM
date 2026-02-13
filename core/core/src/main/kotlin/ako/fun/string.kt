package ako.`fun`

fun String.notEmptyOrNull() = takeIf { isNotEmpty() }