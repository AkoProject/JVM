package ako.annotation.types

import ako.annotation.AkoType
import ako.`fun`.AkoDefaultNode
import ako.`fun`.annotationAnnotation
import ako.protocol.search.SearchEntry
import ako.protocol.type.AkoTypeProvider
import org.intellij.lang.annotations.Language
import java.lang.reflect.Field

@AkoType(Timestamp.TimestampProvider::class)
annotation class Timestamp(@Language("dayjs") val format: String) {
    class TimestampProvider : AkoTypeProvider<Annotation, String, Any> {
        override val id: String
            get() = "ako:timestamp"

        override fun defaultSearch(model: String, id: String, fieldType: Class<*>, width: String, opt: String?) =
            listOf(
                SearchEntry(AkoDefaultNode.fieldSearchInput, "gte", "开始时间", width),
                SearchEntry(AkoDefaultNode.fieldSearchInput, "lte", "结束时间", width),
            )

        override fun readField(
            model: String,
            id: String,
            fieldType: Class<*>,
            nullable: Boolean,
            modelClass: Class<*>?,
            fieldInstance: Field?,
            hitAnnotation: Annotation?,
            typeAnnotation: AkoType?
        ): String? {
            if (hitAnnotation == null) return null
            if (hitAnnotation is Timestamp) return hitAnnotation.format
            return hitAnnotation.annotationAnnotation<Timestamp>().firstOrNull()?.format
        }
    }
}

@AkoType(Timestamp.TimestampProvider::class)
@Timestamp("YYYY-MM-DD")
annotation class DateOnly

@AkoType(Timestamp.TimestampProvider::class)
@Timestamp("HH:mm:ss")
annotation class TimeOnly

@AkoType(Timestamp.TimestampProvider::class)
@Timestamp("YYYY-MM-DD HH:mm:ss")
@Target(AnnotationTarget.FIELD)
annotation class Datetime