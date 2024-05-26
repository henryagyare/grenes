plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    alias(libs.plugins.kotlinPluginSerialization) apply false

    id("com.google.gms.google-services") version "4.4.0" apply false
    id("org.kodein.mock.mockmp") version "1.16.0" apply false
    id("app.cash.sqldelight") version "2.0.1" apply false
    id("org.flywaydb.flyway") version "10.7.2" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("com.google.cloud.tools.appengine") version "2.8.0" apply false
    id("io.sentry.jvm.gradle") version "4.3.0" apply false
}

buildscript {
    dependencies {
        classpath("dev.icerock.moko:resources-generator:0.23.0")
    }
}


tasks.wrapper {
    gradleVersion = "8.5"
    distributionType = Wrapper.DistributionType.ALL
}