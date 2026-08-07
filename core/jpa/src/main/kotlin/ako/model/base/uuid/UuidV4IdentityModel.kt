package ako.model.base.uuid

import ako.annotation.DbName
import ako.annotation.EditIgnore
import ako.annotation.TableColumnWidth
import ako.model.base.AkoModelBase
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.UUID

@MappedSuperclass
open class UuidV4IdentityModel: AkoModelBase() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @DbName("编号")
    @TableColumnWidth(100)
    @EditIgnore
    open var id: UUID? = null
}