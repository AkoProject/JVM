package ako.model.base.l

import ako.annotation.DbName
import ako.annotation.EditIgnore
import ako.annotation.TableColumnWidth
import ako.model.base.AkoModelBase
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
open class LongIdentityModel: AkoModelBase() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DbName("编号")
    @TableColumnWidth(100)
    @EditIgnore
    open var id: Long? = null
}