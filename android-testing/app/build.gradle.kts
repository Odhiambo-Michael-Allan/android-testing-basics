plugins {
    id( "com.android.application" )
    id( "org.jetbrains.kotlin.android" )
    id( "kotlin-kapt" )
    id( "androidx.navigation.safeargs.kotlin" )
}

android {
    namespace = "com.odesa.todo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.odesa.todo"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField( "boolean", "DEBUG", "true" )
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }

    dataBinding {
        enable = true
        enableForTests = true
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
    }
}

dependencies {
    val fragmentVersion = "1.6.2"
    val espressoVersion = "3.5.1"
    val mockitoVersion = "5.7.0"
    val dexMakerVersion = "2.28.3"

    // App Dependencies
    implementation( "androidx.core:core-ktx:1.12.0" )
    implementation( "androidx.appcompat:appcompat:1.6.1" )
    implementation( "com.google.android.material:material:1.10.0" )
    implementation( "androidx.constraintlayout:constraintlayout:2.1.4" )
    implementation( "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0" )
    implementation( "androidx.recyclerview:recyclerview:1.3.2" )
    implementation( "androidx.annotation:annotation:1.7.0" )
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1" )
    implementation( "com.jakewharton.timber:timber:5.0.1" )

    // Architecture Components
    implementation( "androidx.room:room-runtime:2.6.0" )
    kapt( "androidx.room:room-compiler:2.6.0" )
    implementation( "androidx.room:room-ktx:2.6.0" )
    implementation( "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2" )
    implementation( "androidx.lifecycle:lifecycle-livedata-ktx:2.6.2" )
    kapt( "androidx.lifecycle:lifecycle-compiler:2.6.2" )
    implementation( "androidx.navigation:navigation-fragment-ktx:2.7.5" )
    implementation( "androidx.navigation:navigation-ui-ktx:2.7.5" )

    // Dependencies for local unit tests
    testImplementation( "junit:junit:4.13.2" )
    testImplementation( "org.junit.jupiter:junit-jupiter:5.8.1" )
    testImplementation( "org.hamcrest:hamcrest-core:2.2" )

    // Androidx Test - JVM testing
    testImplementation( "androidx.test.ext:junit-ktx:1.1.5" )
    testImplementation( "androidx.test:core-ktx:1.5.0" )
    testImplementation( "org.robolectric:robolectric:4.11.1" )
    testImplementation( "androidx.arch.core:core-testing:2.2.0" )
    testImplementation( "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1" )

    // AndroidX Test - Instrumented testing
    androidTestImplementation( "androidx.test.ext:junit:1.1.5" )
    androidTestImplementation( "androidx.test.espresso:espresso-core:$espressoVersion" )
    androidTestImplementation( "junit:junit:4.13.2" )
    androidTestImplementation( "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1" )
    androidTestImplementation( "org.mockito:mockito-core:$mockitoVersion" )
    androidTestImplementation( "com.linkedin.dexmaker:dexmaker-mockito:$dexMakerVersion" )
    androidTestImplementation( "androidx.test.espresso:espresso-contrib:$espressoVersion" )
    debugImplementation( "androidx.fragment:fragment-testing:$fragmentVersion" )

    
}