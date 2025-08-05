import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")  // kapt 플러그인 직접 선언
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.umcspot.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.umcspot.android"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "BASE_URL", properties["base.url"].toString())
        buildConfigField("String", "KAKAO_NATIVE_KEY", "\"${properties["kakao.native.key"]}\"")
        buildConfigField("String", "NAVER_CLIENT_ID", "\"${properties["naver.client.id"]}\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"${properties["naver.client.secret"]}\"")
        buildConfigField("String", "PUBLIC_API_KEY", "\"${properties["public.api.key"]}\"")

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
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.splashscreen)
    implementation(libs.circleimageview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.flexbox)
    implementation(libs.kakaoSdkAll)
    implementation(libs.kakaoSdkUser)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.media3.common)
    implementation(libs.filament.android)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.viewpager2)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.kotlin.stdlib)
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp("androidx.room:room-compiler:2.6.1")
    implementation ("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("com.navercorp.nid:oauth:5.9.1")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")


}

kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    correctErrorTypes = true
}

configurations {
    all {
        exclude(group = "com.android.support", module = "support-compat")
    }
}