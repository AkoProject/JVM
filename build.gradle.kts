import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.dokka") version "2.0.0"
    java
    `java-library`
    `maven-publish`
    signing
}

group = "com.ako-dev"
version = "0.0.7"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.cnb.cool/IceCream/maven/-/packages/")
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
        plugin("signing")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.dokka")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    dependencies {
//        if (name != "core")
//            api(project(":core"))
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.replace("javadocJar", Jar::class).apply {
        dependsOn(tasks.dokkaJavadoc)
        from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>(name) {
                groupId = rootProject.group.toString()
                artifactId = name
                version = rootProject.version.toString()

                pom {
                    name.set("Ako")
                    description.set("Ako 是一个基于 Kotlin/JVM 与 Kotlin 协程 编写的业务快速启动框架。")
                    url.set("https://github.com/IceCream-QAQ/Ako")
                    licenses {
                        license {
                            name.set("LGPL-2.1 license")
                            url.set("https://github.com/IceCream-QAQ/Ako/blob/master/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("IceCream")
                            name.set("IceCream")
                            email.set("www@withdata.net")
                        }
                    }
                    scm {
                        connection.set("https://github.com/IceCream-QAQ/Ako")
                        developerConnection.set("https://github.com/IceCream-QAQ/Ako")
                        url.set("https://github.com/IceCream-QAQ/Ako")
                    }
                }
                from(components["java"])
            }

            repositories {
                mavenLocal()
                maven {
                    val snapshotsRepoUrl = "https://maven.cnb.cool/IceCream/maven/-/packages/"
                    val releasesRepoUrl = "https://maven.cnb.cool/IceCream/maven/-/packages/"
                    url = uri(releasesRepoUrl)

                    credentials {
                        System.getenv("MAVEN_USER")?.let { username = it }
                        System.getenv("MAVEN_TOKEN")?.let { password = it }
                    }
                }
            }
        }
    }
    signing {
        val key = project.findProperty("signingKey") as String? ?: System.getenv("SIGNING_KEY")
        val password = project.findProperty("signingPassword") as String? ?: System.getenv("SIGNING_PASSWORD")

        if (key != null && password != null) {
            sign(publishing.publications[name])

            useInMemoryPgpKeys(key, password)
        }
    }

}