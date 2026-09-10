dependencies {
    api(project(":ako-core"))

    // These types appear in the public runtime constructor and transaction
    // helper, so they must be available to consumers of the common artifact.
    // The Boot-specific modules add the matching 5/6/7 line and Gradle's
    // normal conflict resolution selects that line for the application.
    api("org.springframework:spring-context:5.3.31")
    api("org.springframework:spring-tx:5.3.31")

    testImplementation("org.springframework:spring-context:5.3.31")
    testImplementation("org.springframework:spring-tx:5.3.31")
    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
}
