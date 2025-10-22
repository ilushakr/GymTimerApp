plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.gymtimerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gymtimerapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.wearable)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
//    implementation(libs.androidx.compose.material)
//    implementation(libs.androidx.compose.foundation)
//    implementation(libs.androidx.wear.tooling.preview)
//    implementation(libs.androidx.core.splashscreen)
//    implementation(libs.androidx.tiles)
//    implementation(libs.androidx.tiles.material)
//    implementation(libs.androidx.tiles.tooling.preview)
//    implementation(libs.horologist.compose.tools)
//    implementation(libs.horologist.tiles)
//    implementation(libs.androidx.watchface.complications.data.source.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//    debugImplementation(libs.androidx.tiles.tooling)

    implementation(project(":multiplatform:stopwatch"))
    implementation(project(":multiplatform:gymtimer"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    val nav_version = "2.9.5"

    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation(libs.koin.android)
    implementation(libs.koin.android.compose)
    implementation("androidx.compose.material:material-navigation:1.9.3")


    implementation(project(":shared:connectivity"))
    implementation(project(":shared:persistent:api"))
    implementation(project(":shared:persistent:room"))
}