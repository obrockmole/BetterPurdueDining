package com.obrockmole.kmpbetterdining.utils

import android.util.Log

actual fun writePlatformLog(logLevel: LogLevel, tag: String, message: String) {
    when (logLevel) {
        LogLevel.DEBUG -> Log.d(tag, message)
        LogLevel.INFO -> Log.i(tag, message)
        LogLevel.WARN -> Log.w(tag, message)
        LogLevel.ERROR -> Log.e(tag, message)
    }
}