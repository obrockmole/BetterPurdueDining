plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.apollo)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.obrockmole.betterdining"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.obrockmole.betterdining"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "BetterPurdueDining-v$versionName")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.apollo.runtime)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

apollo {
    service("service") {
        packageName.set("com.obrockmole.betterdining")
        introspection {
            endpointUrl.set("https://api.hfs.purdue.edu/menus/v3/GraphQL")
            schemaFile.set(file("src/main/java/com/obrockmole/betterdining/graphql/schema.graphqls"))
        }
        srcDir("src/main/java/com/obrockmole/betterdining/graphql")
        mapScalar("ID", "kotlin.String")
        mapScalar("Guid", "kotlin.String")
        mapScalar("TimeOnly", "kotlin.String")
        mapScalar("Uri", "kotlin.String")
        mapScalar("DateTimeOffset", "kotlin.String")
        mapScalar("HexColorCode", "kotlin.String")
        mapScalar("Date", "kotlin.String")
        mapScalar("TimeOnly", "kotlin.String")
        mapScalar("DateTime", "kotlin.String")
        mapScalar("Decimal", "kotlin.String")
    }
}