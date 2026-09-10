package ako.spring.`fun`

import ako.spring.AkoSpringRuntime

/** Execute a block in a REQUIRED Spring transaction when a manager exists. */
inline fun <R> transaction(crossinline block: () -> R): R =
    AkoSpringRuntime.instance.transaction { block() }
