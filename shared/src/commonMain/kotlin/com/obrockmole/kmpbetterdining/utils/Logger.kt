package com.obrockmole.kmpbetterdining.utils

expect fun writePlatformLog(logLevel: LogLevel, tag: String, message: String)

object Logger {
    var logAmount = LogAmount.MINIMAL

    fun LogInfo(tag: String, message: String) {
        if (logAmount == LogAmount.MINIMAL || logAmount == LogAmount.FULL) {
            writePlatformLog(LogLevel.INFO, tag, message)
        }
    }

    fun LogDebug(tag: String, message: String) {
        if (logAmount == LogAmount.FULL) {
            writePlatformLog(LogLevel.DEBUG, tag, message)
        }
    }

    fun LogWarn(tag: String, message: String) {
        if (logAmount == LogAmount.MINIMAL || logAmount == LogAmount.FULL) {
            writePlatformLog(LogLevel.WARN, tag, message)
        }
    }

    fun LogError(tag: String, message: String) {
        if (logAmount == LogAmount.MINIMAL || logAmount == LogAmount.FULL) {
            writePlatformLog(LogLevel.ERROR, tag, message)
        }
    }
}