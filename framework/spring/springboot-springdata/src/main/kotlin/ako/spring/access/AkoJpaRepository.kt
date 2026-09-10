package ako.spring.access

import ako.model.base.AkoModel
import ako.model.base.SoftDeleteModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

/** Compatibility location matching the original Spring integration API. */
@NoRepositoryBean
interface AkoJpaRepository<T : AkoModel, ID : Serializable> : JpaRepository<T, ID>, AkoAccess<T, ID>

@NoRepositoryBean
interface AkoRepository<T : AkoModel, ID : Serializable> : AkoJpaRepository<T, ID>

@NoRepositoryBean
interface AkoSoftDeleteRepository<T : SoftDeleteModel, ID : Serializable> : AkoJpaRepository<T, ID>

@NoRepositoryBean
interface SoftDeleteAccess<T : SoftDeleteModel, ID : Serializable> : AkoJpaRepository<T, ID>
