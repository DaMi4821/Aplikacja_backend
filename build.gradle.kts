val ktor_version = "2.0.0"
val logback_version = "1.2.11"

plugins {
    kotlin("jvm") version "1.9.10"
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
    // Ktor dependencies
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha10")
    

    // Database dependencies
    implementation("org.postgresql:postgresql:42.7.2") // PostgreSQL JDBC driver
    implementation("com.zaxxer:HikariCP:5.0.0") // HikariCP for connection pooling
    implementation("org.jetbrains.exposed:exposed-core:0.41.1") // Exposed ORM core
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1") // Exposed DAO support
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1") // Exposed JDBC support

    // Test dependencies
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.10") // Bezpośrednie określenie wersji Kotlin
}

// Ustawienia kompatybilności Javy bez użycia toolchainów
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// Ustawienie jvmTarget na 17 dla Kotlina
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
