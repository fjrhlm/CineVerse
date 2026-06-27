plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.fjrhlm.cineverse"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.fjrhlm.cineverse"
        minSdk = 28
        targetSdk = 36
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
}

dependencies {
    // Library bawaan (Default)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ==========================================
    // TAMBAHAN LIBRARY UNTUK PROYEK CINEVERSE
    // ==========================================

    // 1. Retrofit & Gson (Untuk menarik data JSON dari TMDB API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 2. Glide (Untuk memuat gambar poster film agar tidak lag)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // 3. Navigation Component (Untuk mengatur alur 10 halaman)
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
}