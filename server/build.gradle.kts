import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinPluginSerialization)
    application
    id("org.flywaydb.flyway")
    id("com.github.johnrengelman.shadow")
    id("com.google.cloud.tools.appengine")
    id("io.sentry.jvm.gradle")
}

group = "me.ayitinya.grenes"
version = "1.0.1"
application {
    mainClass.set("me.ayitinya.grenes.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

val exposedVersion = "0.41.1"
val h2Version = "2.1.214"

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.crypt)
//    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation(libs.h2)

    implementation(libs.hikariCP)
    implementation(libs.ehcache)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.insert.koin.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit5)

    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.status.pages)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.server.test.host)


    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit.jupiter)

    implementation(libs.firebase.admin)
    implementation(libs.guava)
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.7")

    implementation("org.flywaydb:flyway-core:10.7.2")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.15.2")
    implementation(platform("io.sentry:sentry-bom:7.3.0")) //import bom
    implementation("io.sentry:sentry") //no version specified
    implementation("io.sentry:sentry-logback") //no version specified

}

tasks.test {
    useJUnitPlatform()
}

configure<AppEngineAppYamlExtension> {
    stage {
        setArtifact("build/libs/${project.name}-all.jar")
    }
    deploy {
        version = "GCLOUD_CONFIG"
        projectId = "GCLOUD_CONFIG"
    }
}