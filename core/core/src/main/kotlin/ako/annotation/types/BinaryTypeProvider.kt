package ako.annotation.types

import ako.annotation.AkoType
import ako.protocol.type.AkoTypeProvider


@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@AkoType(BinaryTypeProvider::class)
annotation class BinarySize

class BinaryTypeProvider : AkoTypeProvider<BinarySize, Any, Any> {
    override val id: String
        get() = "ako:binary_size"
}