package ako.model.base.l

import ako.annotation.DbName
import ako.annotation.EditIgnore
import ako.annotation.TableColumnWidth
import ako.model.base.SoftDeleteModel
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
open class LongCompleteModel : SoftDeleteModel() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DbName("编号")
    @EditIgnore
    @TableColumnWidth(100)
    open var id: Long? = null
}