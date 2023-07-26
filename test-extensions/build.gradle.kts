val ktor_version: String by project
val kotest_version: String by project
val testcontainers_version: String by project
val hikaricp_version: String by project

plugins {
    kotlin("jvm") version "1.8.22"
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.ktor:ktor-server-tests-jvm:$ktor_version")
    api("io.kotest:kotest-runner-junit5:$kotest_version")
    api("com.github.database-rider:rider-core:1.38.1")
    api("io.mockk:mockk:1.13.5")
    implementation("io.ktor:ktor-client-apache-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("org.testcontainers:testcontainers:$testcontainers_version")
    implementation("org.testcontainers:postgresql:$testcontainers_version")
    implementation("net.javacrumbs.json-unit:json-unit-assertj:2.38.0")
    implementation("net.javacrumbs.json-unit:json-unit:2.38.0")
    implementation("net.javacrumbs.json-unit:json-unit-json-path:2.38.0")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    implementation(project(":micro-service-core"))
    implementation(project(":exposed-extensions"))
}