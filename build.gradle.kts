import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    `java-library`
    `maven-publish`
//    signing
}

repositories {
    mavenCentral()
}

dependencies {
}

allprojects {
    group = "com.dehucka"
    version = "0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
//    apply(plugin = "signing")

    publishing {
        publications {
            create<MavenPublication>(project.name) {
                groupId = project.group as String
                artifactId = project.name
                version = project.version as String

                from(components["java"])
            }
        }
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