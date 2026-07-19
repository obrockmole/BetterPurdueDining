import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.apollo)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
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
    
    androidLibrary {
       namespace = "com.obrockmole.kmpbetterdining.shared"
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
            implementation(libs.jetbrains.material3.adaptive.navigation.suite)
            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

apollo {
    service("service") {
        packageName.set("com.obrockmole.kmpbetterdining")
        introspection {
            endpointUrl.set("https://api.hfs.purdue.edu/menus/v3/GraphQL")
            schemaFile.set(file("src/commonMain/kotlin/com/obrockmole/kmpbetterdining/graphql/schema.graphqls"))
        }
        srcDir("src/commonMain/kotlin/com/obrockmole/kmpbetterdining/graphql")
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