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
    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    //sourceSets["main"].java.srcDirs("src/androidMain/kotlin")
    //sourceSets["main"].res.srcDirs("src/androidMain/res")
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

    //device - ::iosArm64, simulator - ::iosX64
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = ::iosArm64

    iOSTarget("ios") {
        binaries {
            /*framework {
                baseName = "SharedCode"
            }*/
        }
    }

    cocoapods {
        summary = "SharedCode module"
        homepage = "https://mytodolist.relaxeddd.com"
        frameworkName = "SharedCode"

        ios.deploymentTarget = "8.0"

        pod("FirebaseAuth")
    }

    sourceSets {
        val versionCoroutines: String by project
        val versionSerialization: String by project
        val versionSqldelight: String by project
        val versionKtor: String by project

        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versionCoroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${versionSerialization}")
                implementation("io.ktor:ktor-client-core:${versionKtor}")
            }
        }

        named<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>("androidMain") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${versionCoroutines}")
                implementation("com.squareup.sqldelight:android-driver:${versionSqldelight}")
                implementation("io.ktor:ktor-client-android:${versionKtor}")
            }
        }
        named<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>("iosMain") {
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
    //val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    //val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    //val framework = kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)

    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)

    /// generate a helpful ./gradlew wrapper with embedded Java path
    /*doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\n"
                + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
                + "cd '${rootProject.rootDir}'\n"
                + "./gradlew \$@\n")
        gradlew.setExecutable(true)
    }*/
}
tasks.getByName("build").dependsOn(packForXcode)
