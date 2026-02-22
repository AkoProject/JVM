package ako.model.base

import ako.annotation.NoAkoField
import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.Filter

@MappedSuperclass
@Filter(name = "_ako_soft_delete", condition = "delete_time = 0")
abstract class SoftDeleteModel : AkoModelBase() {

    @NoAkoField
    @Column(name = "delete_time")
    @JsonIgnore
    @JSONField(serialize = false)
    open var deleteTime: Long = 0

}