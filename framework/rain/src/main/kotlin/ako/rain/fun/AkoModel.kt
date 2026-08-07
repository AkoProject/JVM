package ako.rain.`fun`

import ako.rain.access.AkoAccess
import ako.model.base.AkoModel
import ako.rain.AkoRain

inline fun <reified T : AkoAccess<*, *>> findAccess(): T =
    AkoRain.instance.accesses.find { T::class.java.isInstance(it) } as T

inline fun <reified T : AkoModel> accessOfModel(modelType: Class<T>) =
    AkoRain.instance.accesses.find { it.modelType == modelType } as AkoAccess<T, *>

inline fun <reified T : AkoModel> T.findAccess() = accessOfModel(T::class.java)

inline fun <reified T : AkoModel> T.save(): T {
    val access = findAccess()
    access.saveOrUpdate(this)
    return this
}

inline fun <reified T : AkoModel> T.delete() =
    findAccess().delete(this)