package com.obrockmole.betterdining.utils

expect fun getPlatform(): Platform

enum class PlatformType {
    ANDROID,
    IOS,
    DESKTOP
}

interface Platform {
    val name: String
    val type: PlatformType
}