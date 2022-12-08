val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val space_version: String by project
val koin_version: String by project
val koin_ktor: String by project
val kotor_client_apache: String by project

plugins {
    application
    kotlin("jvm") version "1.7.22"
    id("io.ktor.plugin") version "2.1.3"
}

group = "com.018bf"
version = "0.1.2"

application {
    mainClass.set("com.018bf.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/space/maven")
}

dependencies {
    implementation("org.jetbrains:space-sdk-jvm:$space_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-ktor:$koin_ktor")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_ktor")
    implementation("io.ktor:ktor-server-core-jvm:2.2.1")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.1")
    implementation("io.ktor:ktor-client-apache-jvm:2.2.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.2.1")
}

tasks.withType<Jar> {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "com.018bf.ApplicationKt"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}