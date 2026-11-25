plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    // --- base de datos ---
    alias(libs.plugins.androidx.room3)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.pagoflex.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.pagoflex.app"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // gateway del backend; 10.0.2.2 es el pc visto desde el emulador.
        // en un celular: pagoflex.apiUrl=http://IP-DEL-PC:8080/ en gradle.properties
        val apiUrl = providers.gradleProperty("pagoflex.apiUrl").getOrElse("http://10.0.2.2:8080/")
        buildConfigField("String", "API_URL", "\"$apiUrl\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.annotation)
    // --- android base ---
    implementation(libs.androidx.core.ktx)

    // --- ciclo de vida y viewmodel ---
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // --- material 3 ---
    implementation(libs.androidx.compose.material3)

    // --- navegación ---
    implementation(libs.androidx.navigation.compose)

    // --- room ---
    implementation(libs.androidx.room3.runtime)
    ksp(libs.androidx.room3.compiler)

    // --- SQLite ---
    implementation(libs.androidx.sqlite.bundled)

    // --- red (backend) ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)


    // --- herramientas de depuración compose ---
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // --- pruebas unitarias ---
    testImplementation(libs.junit)

    // --- pruebas instrumentadas ---
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // --- retrofit dependencias ---
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // --- mostrar foto servidor ---
    implementation("io.coil-kt.coil3:coil-compose:3.4.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.4.0")
}

room3 {
    schemaDirectory("$projectDir/schemas")
}
