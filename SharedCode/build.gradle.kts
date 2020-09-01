import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id( "com.android.library")
    kotlin("multiplatform")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization") version "1.4.0"
}

//----------------------------------------------------------------------------------------------------------------------
android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile(File("src/androidMain/AndroidManifest.xml"))
            java.srcDirs("src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }
    }
}

kotlin {
    android()

    //device - ::iosArm64, simulator - ::iosX64
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = ::iosArm64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "SharedCode"
            }
        }
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
    val targetDir = File(buildDir, "xcode-frameworks")
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets
        .getByName<KotlinNativeTarget>("ios")
        .binaries.getFramework(mode)

    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    from({ framework.outputDirectory })
    into(targetDir)

    /// generate a helpful ./gradlew wrapper with embedded Java path
    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\n"
                + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
                + "cd '${rootProject.rootDir}'\n"
                + "./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}
tasks.getByName("build").dependsOn(packForXcode)
