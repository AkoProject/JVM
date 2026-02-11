package test.ako.model

import ako.access.AkoAccess
import ako.annotation.DbName
import ako.`fun`.findAccess
import ako.`fun`.webError
import ako.model.CompleteModel
import jakarta.persistence.Entity
import jakarta.persistence.Table

@DbName("")
@Entity
@Table
data class School(
) : CompleteModel(){
    companion object: SchoolAccess by findAccess(){
        fun notExist(): Nothing = webError(1000000, "不存在")
        fun alreadyExist(): Nothing = webError(1000001, "已存在")
    }
}

interface SchoolAccess : AkoAccess<School> {
}