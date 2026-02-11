package ako.spring.`fun`

import ako.`fun`.findAccess
import ako.model.db.AkoModel



inline fun <reified T : AkoModel> T.save(): T {
    val access = findAccess()
    if (this.id == null) access.save(this)
    else access.save(this)
    return this
}

inline fun <reified T : AkoModel> T.delete() =
    findAccess().delete(this.id!!)