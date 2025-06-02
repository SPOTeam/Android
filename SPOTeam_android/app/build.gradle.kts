import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
}
val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.example.spoteam_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.spoteam_android"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", properties["base.url"].toString())
        buildConfigField("String", "KAKAO_NATIVE_KEY", "\"${properties["kakao.native.key"]}\"")
        buildConfigField("String", "NAVER_CLIENT_ID", "\"${properties["naver.client.id"]}\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"${properties["naver.client.secret"]}\"")
        buildConfigField("String", "PUBLIC_API_KEY", "\"${properties["public.api.key"]}\"")
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
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    // AndroidX & UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.media3.common)
    implementation(libs.flexbox)

    // Lifecycle & Navigation
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Network & Serialization
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.gson)
    implementation(libs.retrofit.kotlinx.serialization.json)

    // Hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Glide & Calendar & Coil
    implementation(libs.glide)
    implementation(libs.circleimageview)
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")

    // Login SDKs
    implementation(libs.kakaoSdkAll)
    implementation(libs.kakaoSdkUser)
    implementation("com.navercorp.nid:oauth:5.9.1")

    // Date-Time
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")

    // Firebase & Crashlytics
    implementation(libs.firebase.crashlytics.buildtools)

    // 기타
    implementation(libs.filament.android)
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.serialization.json)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    correctErrorTypes = true
}

configurations.all {
    exclude(group = "com.android.support", module = "support-compat")
}
