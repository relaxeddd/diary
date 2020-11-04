buildscript {

    val versionSqldelight: String by project

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0")
        classpath("com.squareup.sqldelight:gradle-plugin:${versionSqldelight}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
