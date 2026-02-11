package ako.rain.`fun`

import ako.rain.AkoRain

inline fun <R> transaction(crossinline block: () -> R): R {
    return AkoRain.instance.transaction(block)
}