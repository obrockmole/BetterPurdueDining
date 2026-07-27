import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.obrockmole.kmpbetterdining.MainKt"

        nativeDistributions {
            macOS {
                targetFormats(TargetFormat.Dmg)
            }
            windows {
                targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            }
            linux {
                targetFormats(TargetFormat.Deb, TargetFormat.AppImage)
                iconFile.set(project.file("src/main/resources/icon.png"))
                menuGroup = "Utility"
            }

            modules("java.sql", "jdk.unsupported")

            packageName = "KMP Better Dining"
            packageVersion = "0.1.1"
            vendor = "obrockmole"
        }

        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}