plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")

    id("org.jetbrains.dokka") version "1.9.20"
}

android {
    namespace = "no.uio.ifi.in2000.team18.airborn"
    compileSdk = 34

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.team18.airborn"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    packaging {
//        pickFirst("META-INF/INDEX.LIST")
//        pickFirst("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

dependencies {
    val navVersion = "2.7.7"
    val ktorVersion = "2.3.9"
    val roomVersion = "2.6.1"

    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("com.google.accompanist:accompanist-permissions:0.24.13-rc")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.dagger:hilt-android:2.49")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("com.mapbox.maps:android:11.3.1")
    implementation("com.mapbox.extension:maps-compose:11.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")
    implementation("io.coil-kt:coil-gif:2.6.0")
    dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:1.9.20")
    implementation("net.engawapg.lib:zoomable:1.6.1")
    implementation("edu.ucar:netcdf4:4.5.5") {
        exclude(group = "commons-logging", module = "commons-logging")
        exclude(group = "org.slf4j", module = "jcl-over-slf4j")
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "com.google.guava", module = "listenablefuture")
        exclude("META-INF/DEPENDENCIES")
    }
    runtimeOnly("edu.ucar:grib:4.5.5") {
        exclude(group = "commons-logging", module = "commons-logging")
        exclude(group = "org.slf4j", module = "jcl-over-slf4j")
        exclude(group = "com.google.guava", module = "guava")
        exclude("META-INF/DEPENDENCIES")
    }
    kapt("com.google.dagger:hilt-android-compiler:2.46")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}
