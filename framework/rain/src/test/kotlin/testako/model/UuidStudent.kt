package testako.model

import ako.rain.access.SoftDeleteAccess
import ako.annotation.DbName
import ako.annotation.types.DbEnum
import ako.annotation.types.Mapping
import ako.`fun`.webError
import ako.model.base.uuid.UuidV4CompleteModel
import ako.rain.`fun`.findAccess
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@DbName("UUID学生")
@Entity
@Table(name = "student_uuid")
@AttributeOverride(
    name = "id",
    column = Column(
        name = "id",
        columnDefinition = "UUID",
        nullable = false,
        updatable = false
    )
)
data class UuidStudent(
    @DbName("小组")
    @Mapping(UuidGroup::class, display = "name")
    @Column(name = "group_id")
    var group: UUID = UUID(0, 0),
    @DbName("姓名")
    var name: String = "",
    @DbName("年龄")
    var age: Int = 0,
    @DbName("性别")
    @DbEnum("男", "女")
    var sex: Boolean = false,
    @DbName("性别2")
    var sex2: SEX = SEX.MAN,
) : UuidV4CompleteModel() {

    enum class SEX {
        @DbName("男")
        MAN,

        @DbName("女")
        WOMAN
    }

    companion object : UuidStudentAccess by findAccess() {
        fun notExist(): Nothing = webError(1000000, "Student不存在}")
        fun alreadyExist(): Nothing = webError(1000001, "Student已存在}")
    }
}

interface UuidStudentAccess : SoftDeleteAccess<UuidStudent, UUID> {
}