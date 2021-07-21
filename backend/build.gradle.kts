import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    maven {
        url = uri("https://www.jetbrains.com/intellij-repository/releases")
    }
    maven {
        url = uri("https://cache-redirector.jetbrains.com/intellij-dependencies")
    }
}

group = "fi.monad"
version = "0.0.1-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.4.32"
    id("com.github.johnrengelman.shadow")
    id("com.squareup.sqldelight")
    application
}

dependencies {
    implementation(Kotlin.stdlib.jdk8)
    implementation(Square.SqlDelight.drivers.jdbcSqlite)
    implementation(Libs.http4k_client_okhttp)
    implementation(Libs.http4k_core)
    implementation(Libs.http4k_format_jackson)
    implementation(Libs.http4k_server_undertow)
    implementation(Libs.hoplite_core)
    implementation(Libs.hoplite_yaml)
    implementation(Libs.nimbus_jose_jwt)
    implementation(Libs.jbcrypt)
    implementation(Libs.kotlin_result)

    testImplementation(Libs.http4k_client_okhttp)
    testImplementation(Libs.http4k_core)
    testImplementation(Libs.http4k_testing_kotest)
    testImplementation(Libs.junit_jupiter_api)
    testImplementation(Libs.junit_jupiter_engine)
}

application {
    mainClassName = "fi.monad.bantamweight.Main.Kt"
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/core/kotlin")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("Bantamweight")
    mergeServiceFiles()
}

sqldelight {
    database("Database") {
        packageName = "fi.monad.bantamweight"
        sourceFolders = listOf("db")
        schemaOutputDirectory = file("src/main/db/schema")
        dialect = "sqlite:3.25"
        verifyMigrations = true
    }
    linkSqlite = false
}

