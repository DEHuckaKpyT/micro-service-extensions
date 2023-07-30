val ktor_version: String by project

plugins {
    kotlin("jvm") version "1.8.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    api("org.quartz-scheduler:quartz:2.3.2")
}