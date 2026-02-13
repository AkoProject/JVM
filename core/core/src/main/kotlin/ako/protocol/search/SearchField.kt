package ako.protocol.search

import ako.protocol.base.BaseField

interface SearchField : BaseField {
    val search: SearchInfo?
}