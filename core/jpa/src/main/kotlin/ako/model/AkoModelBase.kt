package ako.model

import ako.annotation.DbName
import ako.annotation.EditIgnore
import ako.annotation.NoAkoField
import ako.annotation.ValueType
import ako.model.db.AkoModel
import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.hibernate.annotations.FilterDef

@MappedSuperclass
@FilterDef(name = "_ako_soft_delete")
abstract class AkoModelBase : AkoModel {


    @DbName("创建时间")
    @Column(name = "create_time", updatable = false)
    @ValueType(ValueType.Type.DATETIME)
    @EditIgnore
    open var createTime: Long = 0

    @DbName("修改时间")
    @Column(name = "update_time")
    @NoAkoField
    @JsonIgnore
    @JSONField(serialize = false)
    open var updateTime: Long? = null

    @PrePersist
    fun prePersist() {
        createTime = System.currentTimeMillis()
    }

    @PreUpdate
    fun preUpdate() {
        updateTime = System.currentTimeMillis()
    }

}