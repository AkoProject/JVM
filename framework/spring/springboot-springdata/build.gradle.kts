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

val testBootVersion = providers.gradleProperty("akoSpringTestBootVersion").orElse("4.1.1").get()
val testBootModule = if (testBootVersion.startsWith("3.")) ":ako-springboot-3" else ":ako-springboot-4"

dependencies {
    api(project(":ako-spring-common"))
    api(project(":ako-jpa"))

    // Compile against the current Jakarta/Spring Data line. The public API
    // used below is shared by Spring Boot 3's Spring Data 3.x and Boot 4's
    // Spring Data 4.x at runtime.
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:4.1.1")
    compileOnly("org.springframework.data:spring-data-jpa:4.1.1")
    compileOnly("org.springframework:spring-context:7.0.9")
    compileOnly("org.springframework:spring-tx:7.0.9")
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa:$testBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-web:$testBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$testBootVersion")
    testImplementation(project(testBootModule))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("com.h2database:h2:2.4.240")
}

tasks.test {
    useJUnitPlatform()
}
