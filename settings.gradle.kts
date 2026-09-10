rootProject.name = "Ako"

fun includeProject(name: String, dir: String? = null) {
    include(name)
    dir?.let { project(name).projectDir = file(it) }
}

fun core(name: String, dir: String? = null){
    includeProject(":ako-$name", "core/${dir ?: name}")
}

core("core")
//core("framework")
core("jpa")

fun framework(name: String, dir: String? = null) {
    includeProject(":ako-$name", "framework/${dir ?: name}")
}
framework("rain")

// Spring integration is split by the Spring Boot baseline so that the
// common runtime does not leak javax/jakarta or Boot-specific APIs.
framework("spring-common", "spring/common")
framework("springboot-2", "spring/springboot-2")
framework("springboot-3", "spring/springboot-3")
framework("springboot-4", "spring/springboot-4")
framework("springboot-springdata", "spring/springboot-springdata")
