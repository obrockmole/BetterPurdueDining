package com.obrockmole.betterdining.utils

actual fun getPlatform(): Platform = DesktopPlatform()

class DesktopPlatform : Platform {
    override val name: String = System.getProperty("os.name") ?: "Desktop"
    override val type: PlatformType = PlatformType.DESKTOP
}