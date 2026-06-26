package ako.model.base.uuid

import ako.annotation.DbName
import ako.annotation.EditIgnore
import ako.annotation.TableColumnWidth
import ako.model.base.SoftDeleteModel
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UuidGenerator
import org.hibernate.type.SqlTypes
import java.util.UUID

@MappedSuperclass
open class UuidV4CompleteModel : SoftDeleteModel() {
    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    @DbName("编号")
    @EditIgnore
    @TableColumnWidth(200)
    var id: UUID? = null
}