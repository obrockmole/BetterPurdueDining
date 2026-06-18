package com.obrockmole.kmpbetterdining.utils

actual fun writePlatformLog(logLevel: LogLevel, tag: String, message: String) {
    println("[${logLevel.name}] $tag: $message")
}