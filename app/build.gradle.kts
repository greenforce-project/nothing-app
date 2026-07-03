plugins {
    id("com.android.application")
}

android {
    namespace = "com.fadlyas07.donothing"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fadlyas07.donothing"

        minSdk = 21
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            /*
             * Debug signing is used only for personal and
             * development builds.
             */
            signingConfig =
                signingConfigs.getByName("debug")

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
    }
}
