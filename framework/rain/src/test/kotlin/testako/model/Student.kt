package testako.model

import ako.access.SoftDeleteAccess
import ako.annotation.ButtonPanel
import ako.annotation.DbEnum
import ako.annotation.DbFlag
import ako.annotation.DbName
import ako.annotation.Description
import ako.annotation.Mapping
import ako.annotation.ModelButton
import ako.annotation.PanelField
import ako.`fun`.webError
import ako.model.CompleteModel
import ako.rain.`fun`.findAccess
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@DbName("学生")
@Entity
@Table
@ModelButton(
    name = "绩点操作",
    panel = ButtonPanel(
        url = "/api/student/\${id}/point",
        method = "patch",
        fields = [
            PanelField(
                "mode",
                name = DbName("操作模式"),
                description = Description("增加为在当前值上加上设定值（为负时为减少），设定是将当前值设置为设定值。"),
                flag = DbFlag("true:增加", "false:设定")
            ),
            PanelField(
                "point",
                name = DbName("操作值"),
            ),
        ]
    )
)
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