package com.obrockmole.betterdining.utils;

import android.util.Log;

public class Logger {
    public static void LogInfo(String tag, String message) {
        Log.i(tag, message);
    }

    public static void LogDebug(String tag, String message) {
        Log.d(tag, message);
    }

    public static void LogWarning(String tag, String message) {
        Log.w(tag, message);
    }

    public static void LogWarning(String tag, String message, Throwable throwable) {
        Log.w(tag, message, throwable);
    }

    public static void LogError(String tag, String message) {
        Log.e(tag, message);
    }

    public static void LogError(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }
}
