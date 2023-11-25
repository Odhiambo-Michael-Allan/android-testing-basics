// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath( "androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5" )
        // NOTE: Do not place your application dependencies here; they belong in the individual
        // module build.gradle files.
    }
}

plugins {
    id( "com.android.application" ) version "8.1.2" apply false
    id( "org.jetbrains.kotlin.android" ) version "1.9.0" apply false
}

val hamcrestVersion by extra( "2.2" )






