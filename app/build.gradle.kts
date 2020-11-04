plugins {
    id("com.android.application")
    kotlin("android")
    //id("kotlin-android-extensions")
}

android {
    /*signingConfigs {
        config {
            keyAlias 'diaryKey'
            keyPassword 'D5hzw3lo7'
            storeFile file('E:/project/diaryKeystore.jks')
            storePassword 'bd8Tkp01w'
        }
    }*/
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "relaxeddd.simplediary"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "0.1"
    }
    buildFeatures {
        dataBinding = true
    }
    buildTypes {
        getByName("release") {
            //debuggable(true)
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            //signingConfig = signingConfigs.config
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    /*compileOptions {
        sourceCompatibility = 1.8
        targetCompatibility = 1.8
    }*/
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation(project(":SharedCode"))

    implementation("com.google.android.material:material:1.3.0-alpha02")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")

    val versionNav = "2.3.0"
    implementation("androidx.navigation:navigation-fragment-ktx:$versionNav")
    implementation("androidx.navigation:navigation-ui-ktx:$versionNav")
}
