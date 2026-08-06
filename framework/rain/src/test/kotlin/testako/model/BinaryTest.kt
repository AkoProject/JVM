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

@DbName("BinaryTest")
@Entity
@Table(name = "bt2")
data class BinaryTest(
    @DbName("配额")
    @BinarySize
    var point: Long = 0,
) : CompleteModel() {
    companion object : BinaryTestAccess by findAccess() {
        fun notExist(): Nothing = webError(1000000, "BinaryTest不存在}")
        fun alreadyExist(): Nothing = webError(1000001, "BinaryTest已存在}")
    }
}

interface BinaryTestAccess : SoftDeleteAccess<BinaryTest, Int> {
}