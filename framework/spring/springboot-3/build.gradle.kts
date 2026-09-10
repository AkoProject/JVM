java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(project(":ako-spring-common"))

    api("org.springframework.boot:spring-boot-autoconfigure:3.5.16")
    api("org.springframework:spring-context:6.2.19")
    api("org.springframework:spring-tx:6.2.19")
}
