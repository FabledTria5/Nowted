import java.util.Properties

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.google.ksp)
}

val keyStorePropertiesFile = rootProject.file("keystore.properties")

val keyStoreProperties = Properties()

keyStoreProperties.load(keyStorePropertiesFile.inputStream())

android {
    signingConfigs {
        create("release") {
            keyAlias = keyStoreProperties.getProperty("keyAlias") as String
            storeFile = file(keyStoreProperties.getProperty("storeFile") as String)
            storePassword = keyStoreProperties.getProperty("storePassword") as String
            keyPassword = keyStoreProperties.getProperty("keyPassword") as String
        }
    }

    namespace = "dev.fabled.nowted"
    compileSdk = 33

    defaultConfig {
        applicationId = "dev.fabled.nowted"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "dev.fabled.nowted.utils.test_runner.InstrumentationTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Core
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.collections.immutable)
    implementation(libs.timber)
    implementation(libs.preferences.datastore)
    coreLibraryDesugaring(libs.desugaring)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.ui)
    implementation(libs.compose.material.icons)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Accompanist
    implementation(libs.accompanist.controller)
    implementation(libs.accompanist.permission)

    // Navigation
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.androidx)
    implementation(libs.voyager.koin)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DI
    implementation(libs.koin.compose)
    testImplementation(libs.koin.test)
    androidTestImplementation(libs.koin.android.test)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Compose tests
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

}