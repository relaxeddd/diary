import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id( "com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization") version "1.4.10"
}

//----------------------------------------------------------------------------------------------------------------------
android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(21)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

kotlin {
    android()

    ios {
        binaries {
            //framework {
                //baseName = "SharedCode"
            //}
        }
    }


    cocoapods {
        summary = "SharedCode module"
        homepage = "https://mytodolist.relaxeddd.com"
        frameworkName = "SharedCode"

        ios.deploymentTarget = "14.1"

        pod("FirebaseAuth")
    }

    sourceSets {
        val versionCoroutines: String by project
        val versionSerialization: String by project
        val versionSqldelight: String by project
        val versionKtor: String by project

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versionCoroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${versionSerialization}")
                implementation("io.ktor:ktor-client-core:${versionKtor}")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${versionCoroutines}")
                implementation("com.squareup.sqldelight:android-driver:${versionSqldelight}")
                implementation("io.ktor:ktor-client-android:${versionKtor}")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:${versionSqldelight}")
                implementation("io.ktor:ktor-client-ios:${versionKtor}")
            }
        }
    }
}

sqldelight {
    database("Database") {
        packageName = "relaxeddd.simplediary"
    }
}

//----------------------------------------------------------------------------------------------------------------------
val packForXcode by tasks.creating(Sync::class) {
    group = "build"

    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)

    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}
tasks.getByName("build").dependsOn(packForXcode)
