plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.rouyashiki.ducktogay"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.rouyashiki.ducktogay"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly(libs.libxposed.api)
}
