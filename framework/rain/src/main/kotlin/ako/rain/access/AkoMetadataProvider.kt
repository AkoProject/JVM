package ako.access

import smartaccess.access.Access
import smartaccess.access.AccessMetadataProvider
import java.lang.reflect.ParameterizedType

class AkoMetadataProvider : AccessMetadataProvider {

    override fun getAccessModelType(accessClass: Class<out Access<*, *>>): Class<*> {
        return (accessClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0] as Class<*>
    }

    override fun getAccessPrimaryKeyType(accessClass: Class<out Access<*, *>>): Class<*> {
        return Int::class.javaObjectType
    }
}