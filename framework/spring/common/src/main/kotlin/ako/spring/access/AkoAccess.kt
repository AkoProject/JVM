package ako.spring.access

import ako.model.base.AkoModel
import java.io.Serializable

/**
 * A framework-neutral marker for a Spring Data or custom data access bean.
 *
 * The common module deliberately does not extend Spring Data's Repository
 * interfaces. That keeps this API usable with Boot 2, 3, 4, or a completely
 * different database implementation.
 */
interface AkoAccess<T : AkoModel, PK : Serializable>
