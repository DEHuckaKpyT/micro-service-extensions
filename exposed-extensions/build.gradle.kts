val exposed_version: String by project

plugins {
    kotlin("jvm") version "1.8.22"
}

repositories {
    mavenCentral()
}

dependencies {
    //region database
    api("org.jetbrains.exposed:exposed-core:$exposed_version")
    api("org.jetbrains.exposed:exposed-dao:$exposed_version")
    api("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    //endregion database
}