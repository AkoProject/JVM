package ako.spring.access

import ako.access.AkoAccess
import ako.model.db.AkoModel
import org.springframework.data.jpa.repository.JpaRepository

interface AkoRepository<T : AkoModel> : JpaRepository<T, Int>, AkoAccess<T> {

}