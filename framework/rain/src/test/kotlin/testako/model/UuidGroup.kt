package testako.model

import ako.access.SoftDeleteAccess
import ako.annotation.DbName
import ako.`fun`.webError
import ako.model.base.uuid.UuidV4CompleteModel
import ako.rain.`fun`.findAccess
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@DbName("UUID小组")
@Entity
@Table(name = "class_group_uuid")
@AttributeOverride(
    name = "id",
    column = Column(
        name = "id",
        columnDefinition = "UUID",
        nullable = false,
        updatable = false
    )
)
data class UuidGroup(
    var name: String = "",
    var description: String = ""
) : UuidV4CompleteModel() {
    companion object : UuidGroupAccess by findAccess() {
        fun notExist(): Nothing = webError(1000000, "Group不存在")
        fun alreadyExist(): Nothing = webError(1000001, "Group已存在")
    }
}

interface UuidGroupAccess : SoftDeleteAccess<UuidGroup, UUID> {
}