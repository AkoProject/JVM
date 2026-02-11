package ako.`fun`

import java.lang.reflect.AnnotatedElement


inline fun <reified T : Annotation> AnnotatedElement.annotation(): T? = getAnnotation(T::class.java)

inline fun <reified T : Annotation> AnnotatedElement.annotation(body: T.() -> Unit): T? =
    getAnnotation(T::class.java)?.apply(body)

inline fun <reified T : Annotation> AnnotatedElement.hasAnnotation(): Boolean =
    getAnnotation(T::class.java)?.let { true } ?: false