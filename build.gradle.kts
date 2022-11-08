plugins {
    kotlin("jvm") version "1.6.10"
    id("io.gatling.gradle") version "3.8.4"
}

group = "de.novatec"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.seleniumhq.selenium:selenium-java:4.4.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.3.0")
}

gatling {
//    logLevel = "WARN"
//    logHttp = "NONE"
}