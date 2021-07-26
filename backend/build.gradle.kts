import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.4.32"
    id("com.squareup.sqldelight")
    id("com.github.johnrengelman.shadow")
}

group = "fi.monad"

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

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClassName = "fi.monad.MainKt"
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs += listOf(
                "-Xjsr305=strict"
            )
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set(project.name)
        mergeServiceFiles()
    }
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
