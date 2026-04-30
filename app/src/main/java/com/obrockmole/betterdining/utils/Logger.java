package com.obrockmole.betterdining.utils;

import android.util.Log;

public class Logger {
    private static LogLevel logLevel = LogLevel.MINIMAL;

    public static void setLogLevel(String level) {
        try {
            logLevel = LogLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            Log.e("Logger", "Invalid log level: " + level + ". " + e.getMessage());
            logLevel = LogLevel.MINIMAL;
        }
    }

    public static void LogInfo(String tag, String message) {
        if (logLevel == LogLevel.MINIMAL || logLevel == LogLevel.FULL) {
            Log.i(tag, message);
        }
    }

    public static void LogDebug(String tag, String message) {
        if (logLevel == LogLevel.FULL) {
            Log.d(tag, message);
        }
    }

    public static void LogWarning(String tag, String message) {
        if (logLevel == LogLevel.MINIMAL || logLevel == LogLevel.FULL) {
            Log.w(tag, message);
        }
    }

    public static void LogWarning(String tag, String message, Throwable throwable) {
        if (logLevel == LogLevel.MINIMAL || logLevel == LogLevel.FULL) {
            Log.w(tag, message, throwable);
        }
    }

    public static void LogError(String tag, String message) {
        if (logLevel == LogLevel.MINIMAL || logLevel == LogLevel.FULL) {
            Log.e(tag, message);
        }
    }

    public static void LogError(String tag, String message, Throwable throwable) {
        if (logLevel == LogLevel.MINIMAL || logLevel == LogLevel.FULL) {
            Log.e(tag, message, throwable);
        }
    }
}
