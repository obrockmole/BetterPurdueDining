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
            }

            modules("java.sql", "jdk.unsupported")

            packageName = "com.obrockmole.kmpbetterdining"
            packageVersion = "0.1.1"
        }

        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}