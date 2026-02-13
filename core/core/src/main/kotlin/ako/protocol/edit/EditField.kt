package ako.protocol.edit

import ako.protocol.base.BaseField

interface EditField : BaseField {
    val edit: EditInfo?
}