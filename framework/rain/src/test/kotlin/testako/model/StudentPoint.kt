package testako.model

import ako.access.SoftDeleteAccess
import ako.annotation.DbName
import ako.annotation.Mapping
import ako.`fun`.webError
import ako.model.CompleteModel
import ako.rain.`fun`.findAccess
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@DbName("学生绩点")
@Entity
@Table
data class StudentPoint(
    @DbName("小组")
    @Mapping(Group::class, display = "name")
    @Column(name = "group_id")
    var group: Int = 0,
    @DbName("学生")
    @Mapping(Group::class, display = "name")
    @Column(name = "student_id")
    var student: Int = 0,
    @DbName("绩点")
    var point: Int = 0,
) : CompleteModel() {
    companion object : StudentPointAccess by findAccess() {
        fun notExist(): Nothing = webError(1000000, "Student不存在}")
        fun alreadyExist(): Nothing = webError(1000001, "Student已存在}")
    }
}

interface StudentPointAccess : SoftDeleteAccess<StudentPoint> {
    fun findByStudent(student: Int): StudentPoint?
}