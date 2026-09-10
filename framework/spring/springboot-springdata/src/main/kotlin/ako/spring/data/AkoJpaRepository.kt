package ako.spring.data

import ako.model.base.AkoModel
import ako.model.base.SoftDeleteModel
import ako.spring.access.AkoAccess
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

/**
 * Base repository for an Ako model. Custom query methods can be declared on
 * the application's repository interface as usual.
 */
@NoRepositoryBean
interface AkoJpaRepository<T : AkoModel, ID : Serializable> : JpaRepository<T, ID>, AkoAccess<T, ID>

/** Short name retained for the usual Spring Data repository style. */
@NoRepositoryBean
interface AkoRepository<T : AkoModel, ID : Serializable> : AkoJpaRepository<T, ID>

/** Marker base for repositories whose entities extend Ako's soft-delete model. */
@NoRepositoryBean
interface AkoSoftDeleteRepository<T : SoftDeleteModel, ID : Serializable> : AkoJpaRepository<T, ID>

/** Name-parity with the Rain integration for soft-delete repositories. */
@NoRepositoryBean
interface SoftDeleteAccess<T : SoftDeleteModel, ID : Serializable> : AkoJpaRepository<T, ID>
