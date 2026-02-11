package testako.model

import ako.access.SoftDeleteAccess
import ako.annotation.DbName
import ako.`fun`.webError
import ako.model.CompleteModel
import ako.rain.`fun`.findAccess
import jakarta.persistence.Entity
import jakarta.persistence.Table

@DbName("小组")
@Entity
@Table(name = "class_group")
data class Group(
    var name: String = "",
    var description: String = ""
) : CompleteModel() {
    companion object : GroupAccess by findAccess() {
        fun notExist(): Nothing = webError(1000000, "Group不存在")
        fun alreadyExist(): Nothing = webError(1000001, "Group已存在")
    }
}

interface GroupAccess : SoftDeleteAccess<Group> {
}