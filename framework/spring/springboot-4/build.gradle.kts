java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(project(":ako-spring-common"))

    api("org.springframework.boot:spring-boot-autoconfigure:4.1.1")
    api("org.springframework:spring-context:7.0.9")
    api("org.springframework:spring-tx:7.0.9")
}
