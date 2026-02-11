package ako.spring.`fun`

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

val om = ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//    .registerModule(KotlinModule.Builder().build())

fun Any.toJsonString(): String = om.writeValueAsString(this)

fun <T> String.toObject(clazz: Class<T>): T = om.readValue(this, clazz)!!
inline fun <reified T> String.toObject(): T = om.readValue(this, T::class.java)!!


fun <T> JsonNode.toObject(clazz: Class<T>): T = om.treeToValue(this, clazz)!!
inline fun <reified T> JsonNode.toObject(): T = om.treeToValue(this, T::class.java)!!