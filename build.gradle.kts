val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.FitnessKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.0.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.0.0")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.0.0")

    // Postgress
    implementation("org.postgresql:postgresql:42.7.2") // PostgreSQL JDBC driver
    implementation("com.zaxxer:HikariCP:5.0.0") // HikariCP for connection pooling
    implementation("org.jetbrains.exposed:exposed-core:0.41.1") // Exposed ORM core
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1") // Exposed DAO support
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1") // Exposed JDBC support

    // Testowanie
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}


