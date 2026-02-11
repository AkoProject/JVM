java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":ako-jpa"))
    implementation("org.springframework.boot:spring-boot-starter:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.0")
}