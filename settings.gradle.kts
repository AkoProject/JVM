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
//framework("spring-boot-3-jpa")