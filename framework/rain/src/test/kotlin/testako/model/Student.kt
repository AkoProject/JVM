package testako.model

import ako.access.SoftDeleteAccess
import ako.annotation.DbEnum
import ako.annotation.DbName
import ako.annotation.Mapping
import ako.`fun`.webError
import ako.model.CompleteModel
import ako.rain.`fun`.findAccess
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@DbName("学生")
@Entity
@Table
data class Student(
    @DbName("小组")
    @Mapping(Group::class, display = "name")
    @Column(name = "group_id")
    var group: Int = 0,
    @DbName("姓名")
    var name: String = "",
    @DbName("年龄")
    var age: Int = 0,
    @DbName("性别")
    @DbEnum("男", "女")
    var sex: Boolean = false,
) : CompleteModel() {
    companion object : StudentAccess by findAccess() {
        fun notExist(): Nothing = webError(1000000, "Student不存在}")
        fun alreadyExist(): Nothing = webError(1000001, "Student已存在}")
    }
}

interface StudentAccess : SoftDeleteAccess<Student> {
}