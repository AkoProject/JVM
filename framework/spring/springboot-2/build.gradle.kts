dependencies {
    api(project(":ako-spring-common"))

    api("org.springframework.boot:spring-boot-autoconfigure:2.7.18")
    api("org.springframework:spring-context:5.3.31")
    api("org.springframework:spring-tx:5.3.31")

    testImplementation("org.springframework.boot:spring-boot-starter:2.7.18")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.18")
    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
}
