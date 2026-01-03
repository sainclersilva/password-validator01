plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    application
}

group = "com.desafio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.7")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.7")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.7")
    testImplementation("io.ktor:ktor-client-content-negotiation-jvm:2.3.7")
}

application {
    mainClass.set("org.example.AppKt")
}

kotlin {
    jvmToolchain(17) 
}