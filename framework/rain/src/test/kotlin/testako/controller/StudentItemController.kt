package testako.controller

import ako.rain.`fun`.save
import ako.rain.`fun`.transaction
import rain.controller.annotation.Path
import smartweb.annotation.PatchAction
import smartweb.annotation.WebController
import testako.model.Student
import testako.model.StudentPoint

@WebController
@Path("api/student/{studentId}")
class StudentItemController {

    @PatchAction("point")
    fun point(studentId: Int, point: Int, mode: Int) = transaction {
        val student = Student.get(studentId) ?: Student.notExist()
        run { StudentPoint.findByStudent(studentId) ?: StudentPoint(student.group, studentId, 0) }
            .apply { if (mode == 0) this.point += point else this.point = point }
            .save()
    }

}