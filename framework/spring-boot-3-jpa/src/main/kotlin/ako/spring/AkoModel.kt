package ako.spring

import ako.AkoFramework
import ako.access.AkoAccess
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

inline fun <reified T : AkoAccess<*>> findAccess(): T = createAccessProxy(T::class.java)


class AccessWrapperProxy<T : AkoAccess<*>>(private val type: Class<T>) : InvocationHandler {
    private var _instance: T? = null

    private val instance: T
        get() = _instance
            ?: (AkoFramework.modelMap.values.find { type.isInstance(it.access) }?.access as? T)
                ?.apply { _instance = this }
            ?: error("无法找到 Access: ${type.name}")

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        return if (args != null) method.invoke(instance, *args)
        else method.invoke(instance)
    }
}

fun <T : AkoAccess<*>> createAccessProxy(type: Class<T>): T {
    return java.lang.reflect.Proxy.newProxyInstance(
        type.classLoader,
        arrayOf(type),
        AccessWrapperProxy(type)
    ) as T
}