val ktor_version: String by project
val eureka_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.8.22"
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    api("io.ktor:ktor-server-core-jvm:$ktor_version")
    api("io.ktor:ktor-serialization-jackson-jvm:$ktor_version")
    api("io.ktor:ktor-client-apache-jvm:$ktor_version")
    api("io.ktor:ktor-client-content-negotiation:$ktor_version")
    api("io.ktor:ktor-server-netty-jvm:$ktor_version")

    api("ch.qos.logback:logback-classic:$logback_version")
}