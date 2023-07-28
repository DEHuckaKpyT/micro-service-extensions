import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    `java-library`
    `maven-publish`
    signing
    id("io.codearte.nexus-staging") version ("0.30.0")
}

repositories {
    mavenCentral()
}

dependencies {
}

nexusStaging {
    serverUrl = "https://s01.oss.sonatype.org/service/local/"
    username = project.properties["ossrh-username"].toString()
    password = project.properties["ossrh-password"].toString()
}

allprojects {
    group = "io.github.dehuckakpyt"
    version = "0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    publishing {
        publications {
            create<MavenPublication>(project.name) {
                groupId = project.group as String
                artifactId = project.name
                version = project.version as String

                from(components["java"])

                pom {
                    packaging = "jar"
                    name.set("microservice-extensions")
                    description.set("library with extensions for developing microservices on kotlin (ktor, exposed, eureka)")
                    url.set("https://github.com/DEHuckaKpyT/microservice-extensions")
                    scm {
                        connection.set("scm:https://github.com/DEHuckaKpyT/microservice-extensions.git")
                        developerConnection.set("scm:git@github.com:DEHuckaKpyT/microservice-extensions.git")
                        url.set("https://github.com/DEHuckaKpyT/microservice-extensions")
                    }
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("DEHuckaKpyT")
                            name.set("Denis Matytsin")
                            email.set("den-matytsin@mail.com")
                        }
                    }
                }
                repositories {
                    maven {
                        val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
                        credentials {
                            username = project.properties["ossrh-username"].toString()
                            password = project.properties["ossrh-password"].toString()
                        }
                    }
                }
            }
        }
    }

    signing {
        sign(publishing.publications[project.name])
    }

    java {
        withJavadocJar()
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    sourceSets.main {
        java.srcDirs("build/generated/ksp/main/kotlin")
    }
}