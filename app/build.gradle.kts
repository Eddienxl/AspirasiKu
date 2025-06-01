plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.pmob.aspirasiku"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pmob.aspirasiku"
        minSdk = 21
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
        // Penting: Sesuaikan dengan Java version yang Anda gunakan di project
        sourceCompatibility = JavaVersion.VERSION_1_8 // Umumnya masih 1.8 untuk Android
        targetCompatibility = JavaVersion.VERSION_1_8 // Atau JavaVersion.VERSION_11 jika Anda yakin
    }
    // Jika Anda menggunakan Kotlin, tambahkan kotlinOptions
    // kotlinOptions {
    //     jvmTarget = '1.8'
    // }
}

dependencies {

    // AndroidX Core & UI
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    // Firebase (Messaging)
    implementation(libs.firebase.messaging)
    // Jika Anda menggunakan platform Firebase BOM, ini akan lebih baik:
    // implementation(platform(libs.firebase.bom))
    // implementation(libs.firebase.messaging)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit & Gson Converter
    implementation(libs.retrofit)
    implementation(libs.converter.gson) // Untuk parsing JSON dengan Retrofit

    // RecyclerView
    implementation(libs.recyclerview)

    // CardView
    implementation(libs.cardview)

    // Glide untuk memuat gambar
    // PASTIKAN NAMA ALIAS DI libs.versions.toml SUDAH BENAR UNTUK GLIDE
    implementation(libs.glide)
    annotationProcessor(libs.compiler) // Untuk Glide v4.x, ini adalah compiler-nya

    // Material Design (v1.1.10 sudah cukup lama, v1.12.0 adalah yang terbaru)
    // Jika Anda menggunakan v1.11.0 dari libs, ini sudah ok.
    // implementation(libs.material.v1110) // Tergantung alias Anda di libs.versions.toml
    // Jika tidak ada alias spesifik, gunakan ini:
    // implementation 'com.google.android.material:material:1.12.0'

    // Android Navigation Component (Pastikan ini sesuai dengan libs.versions.toml)
    // Alias yang umum adalah 'androidx.navigation.fragment.ktx' dan 'androidx.navigation.ui.ktx'
    // Jika Anda menggunakan aliases dari 'libs', pastikan aliases tersebut merujuk ke library yang benar.
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Dependency untuk CircleImageView (jika Anda menggunakannya, ini perlu ditambahkan secara manual
    // karena belum ada di libs.versions.toml yang Anda tunjukkan)
    // implementation 'de.hdodenhof:circleimageview:3.1.0' // Atau versi terbaru

}