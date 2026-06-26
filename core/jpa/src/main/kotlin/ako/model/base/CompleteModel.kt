package ako.model.base

import ako.annotation.DbName
import ako.annotation.EditIgnore
import ako.annotation.TableColumnWidth
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
open class CompleteModel : SoftDeleteModel() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DbName("编号")
    @EditIgnore
    @TableColumnWidth(100)
    var id: Int? = null
}