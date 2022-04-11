import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.jairman"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
    //implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    //https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api-kotlin
    //implementation("org.apache.logging.log4j:log4j-api-kotlin:1.1.0")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}