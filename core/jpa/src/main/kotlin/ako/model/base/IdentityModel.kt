package ako.model.base

import ako.annotation.DbName
import ako.annotation.SearchIgnore
import ako.annotation.TableColumnWidth
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
open class IdentityModel: AkoModelBase() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DbName("编号")
    @TableColumnWidth(100)
    @SearchIgnore
    override var id: Int? = null
}