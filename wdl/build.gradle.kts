plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

group = "com.github.niusounds"
version = "0.0.1"

android {
    compileSdk = 31

    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.findByName("release"))
                artifactId = "wdl-android"
            }
        }
    }
}