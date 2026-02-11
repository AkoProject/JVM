package ako.`fun`

import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Modifier


val Member.isStatic get() = Modifier.isStatic(modifiers)
val Class<*>.allField: List<Field>
    get() {
        val list = ArrayList<Field>()
        var clazz: Class<*>? = this
        while (clazz != null) {
            list.addAll(clazz.declaredFields)
            clazz = clazz.superclass
        }
        return list
    }