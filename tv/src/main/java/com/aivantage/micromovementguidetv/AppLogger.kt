package com.aivantage.micromovementguidetv

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLogger {

    private const val LOG_FILE_NAME = "app_log.txt"

    fun log(context: Context, message: String) {
        try {
            val logFile = File(context.filesDir, LOG_FILE_NAME)
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            logFile.appendText("[$timestamp] $message\n")
        } catch (e: Exception) {
            // Don't crash the app if logging fails
            e.printStackTrace()
        }
    }
}
