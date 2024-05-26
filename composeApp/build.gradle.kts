import dev.icerock.gradle.MRVisibility
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)

    alias(libs.plugins.kotlinPluginSerialization)
    id("app.cash.sqldelight")
//    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    listOf(
        iosX64(), iosArm64(), iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.multiplatform.settings.datastore)
            implementation(libs.accompanist.systemuicontroller)

            implementation(libs.androidx.activity)

            implementation(libs.koin.android)

            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:32.7.0"))
//            implementation("androidx.compose.material3:material3-android:1.2.0-rc01")
            // Add the dependency for the Firebase Authentication library
            // When using the BoM, you don't specify versions in Firebase library dependencies
            implementation("com.google.firebase:firebase-auth")
            implementation(libs.gms.play.services.auth)
            // Also add the dependency for the Google Play services library and specify its version
            implementation("com.google.firebase:firebase-dynamic-links")

            api("androidx.startup:startup-runtime:1.1.1")

            implementation("app.cash.sqldelight:android-driver:2.0.1")

            api("dev.icerock.moko:resources:0.23.0")
            api("dev.icerock.moko:resources-compose:0.23.0")

        }

        commonTest.dependencies {
            implementation(libs.koin.test)
            implementation(libs.multiplatform.settings.test)
            implementation(libs.kotlin.test.junit)

            implementation("app.cash.paging:paging-testing:3.3.0-alpha02-0.4.0")
        }

        commonMain.dependencies {
            implementation(projects.shared)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

            implementation(compose.materialIconsExtended)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.serialization.kotlinx.json)


            implementation(libs.insert.koin.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.precompose)
            implementation(libs.precompose.viewmodel)
            implementation(libs.precompose.koin)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)

            implementation(libs.material3.window.sizeclass.multiplatform)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)


            implementation(libs.firebase.auth)

            implementation(libs.paging.common)
            implementation(libs.paging.compose.common)

            implementation(libs.kotlin.reflect)

            api(libs.androidx.datastore.preferences.core)
            api(libs.androidx.datastore.core)

            implementation(libs.mpfilepicker)

            implementation(libs.coroutines.extensions)

            implementation(libs.kamel.image)
            implementation(libs.calf.file.picker)

//            implementation("com.airbnb.android:lottie-compose:6.3.0")
            implementation(libs.compottie)
//            implementation("io.github.ismai117:kottie:1.4.3")
            implementation("dev.gitlive:firebase-storage:1.11.1")
            implementation(libs.materii.pullrefresh)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "me.ayitinya.grenes"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "me.ayitinya.grenes"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
        implementation(libs.androidx.core.splashscreen)
    }

    apply(plugin = "com.google.gms.google-services")
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("me.ayitinya.grenes")
        }
    }
}

//multiplatformResources {
//    multiplatformResourcesPackage = "me.ayitinya.grenes" // required
//    multiplatformResourcesClassName = "SharedRes" // optional, default MR
//    multiplatformResourcesVisibility = MRVisibility.Internal // optional, default Public
////    iosBaseLocalizationRegion = "en" // optional, default "en"
////    multiplatformResourcesSourceSet = "commonClientMain"  // optional, default "commonMain"
//}