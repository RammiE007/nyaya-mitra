  GNU nano 9.1                  build.gradle.kts                  
Modified
// Top-level build file where you can add configuration options 
// common to a>
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.google.services) apply false
}
android {
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = 
rootProject.file("key.properties")
            val keystoreProperties = java.util.Properties()
            keystoreProperties.load(java.io.FileInputStream(keystorePropert>

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as 
String
            storeFile = file(keystoreProperties["storeFile"] as 
String)
            storePassword = keystoreProperties["storePassword"] as 
String
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

