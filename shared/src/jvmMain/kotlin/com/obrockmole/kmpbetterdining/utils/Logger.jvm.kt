package com.obrockmole.kmpbetterdining.utils

actual fun writePlatformLog(logLevel: LogLevel, tag: String, message: String) {
    val white = ""
    val grey = "\u001B[38;5;250m"
    val yellow = "\u001B[33m"
    val red = "\u001B[31m"
    val reset = "\u001B[0m"

    val color = when (logLevel) {
        LogLevel.INFO -> white
        LogLevel.DEBUG -> grey
        LogLevel.WARN -> yellow
        LogLevel.ERROR -> red
    }

    println("${color}[${logLevel.name}] $tag: $message${reset}")
}