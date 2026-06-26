package testako.model

import ako.access.SoftDeleteAccess
import ako.annotation.ButtonPanel
import ako.annotation.DbName
import ako.annotation.Description
import ako.annotation.types.Mapping
import ako.annotation.ModelButton
import ako.annotation.PanelField
import ako.annotation.types.BinarySize
import ako.annotation.types.DbEnum
import ako.`fun`.webError
import ako.model.base.CompleteModel
import ako.rain.`fun`.findAccess
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@DbName("学生绩点")
@Entity
@Table
@ModelButton(
    name = "绩点操作",
    panel = ButtonPanel(
        url = "/api/student/\${student}/point",
        method = "patch",
        fields = [
            PanelField(
                "mode",
                name = DbName("操作模式"),
                nullable = false,
                description = Description("增加为在当前值上加上设定值（为负时为减少），设定是将当前值设置为设定值。"),
                enum = DbEnum("增加", "设定")
            ),
            PanelField(
                "point",
                name = DbName("操作值"),
                nullable = false,
            ),
        ]
    )
)
data class StudentPoint(
    @DbName("小组")
    @Mapping(Group::class, display = "name")
    @Column(name = "group_id")
    var group: Int = 0,
    @DbName("学生")
    @Mapping(Student::class, display = "name")
    @Column(name = "student_id")
    var student: Int = 0,
    @DbName("配额")
    var point: Int = 0,
) : CompleteModel() {
    companion object : StudentPointAccess by findAccess() {
        fun notExist(): Nothing = webError(1000000, "Student不存在}")
        fun alreadyExist(): Nothing = webError(1000001, "Student已存在}")
    }
}

interface StudentPointAccess : SoftDeleteAccess<StudentPoint, Int> {
    fun findByStudent(student: Int): StudentPoint?
}