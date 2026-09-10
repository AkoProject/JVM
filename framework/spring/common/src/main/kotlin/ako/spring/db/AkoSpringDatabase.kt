package ako.spring.db

import ako.model.base.AkoModel
import ako.model.resp.PageResp
import ako.protocol.db.DbModel

/**
 * The database SPI used by the Spring runtime.
 *
 * Implementations only need to expose the model types they own and implement
 * the five basic operations below. The rest of Ako (metadata, menus, mapping
 * information and lifecycle hooks) stays in the common module.
 */
interface AkoSpringDatabase {

    /** Models served by this database implementation. */
    val modelTypes: Collection<Class<out AkoModel>>

    fun <T : AkoModel> whereList(
        model: DbModel<out T>,
        params: Map<String, Any>? = null,
        sort: Map<String, String>? = null,
        page: Int? = null,
        size: Int? = null,
    ): List<T>

    fun <T : AkoModel> wherePage(
        model: DbModel<out T>,
        params: Map<String, Any>? = null,
        sort: Map<String, String>? = null,
        page: Int,
        size: Int,
    ): PageResp<T>

    fun <T : AkoModel> save(data: T): T

    fun delete(model: DbModel<out AkoModel>, id: Any?)
}

/** A shorter alias for applications that prefer the generic database name. */
typealias AkoDatabase = AkoSpringDatabase
