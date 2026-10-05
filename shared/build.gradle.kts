import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.apollo)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    android {
       namespace = "com.obrockmole.betterdining.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }

       androidResources {
           enable = true
       }

       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
            implementation(libs.sqldelight.native)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.desktop)
            implementation(libs.sqldelight.sqlite)
        }

        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.apollo.runtime)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.datastore.preferences.core)
            implementation(libs.jetbrains.material3.adaptive.navigation.suite)
            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.okio)
            implementation(libs.sqldelight.coroutines)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

apollo {
    service("service") {
        packageName.set("com.obrockmole.betterdining")
        introspection {
            endpointUrl.set("https://api.hfs.purdue.edu/menus/v3/GraphQL")
            schemaFile.set(file("src/commonMain/kotlin/com/obrockmole/betterdining/graphql/schema.graphqls"))
        }
        srcDir("src/commonMain/kotlin/com/obrockmole/betterdining/graphql")
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

sqldelight {
    databases {
        create("BetterDiningDatabase") {
            packageName.set("com.obrockmole.betterdining.database")
        }
    }
}