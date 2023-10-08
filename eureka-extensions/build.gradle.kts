val ktor_version: String by project
val eureka_version: String by project
val koin_version: String by project

plugins {
    kotlin("jvm") version "1.8.22"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    api("io.ktor:ktor-client-apache-jvm:$ktor_version")
    api("io.ktor:ktor-client-content-negotiation:$ktor_version")
    api("com.netflix.eureka:eureka-client:$eureka_version")
    compileOnlyApi("io.insert-koin:koin-core-coroutines:$koin_version")

    implementation(project(":micro-service-core"))
}