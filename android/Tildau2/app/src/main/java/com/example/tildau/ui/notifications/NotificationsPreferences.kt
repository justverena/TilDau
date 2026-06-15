package com.example.tildau.ui.notifications

import android.content.Context

class NotificationPreferences(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "notification_prefs",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val KEY_ENABLED = "enabled"
        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"
    }

    fun isEnabled(): Boolean {
        return prefs.getBoolean(
            KEY_ENABLED,
            false
        )
    }

    fun setEnabled(
        enabled: Boolean
    ) {
        prefs.edit()
            .putBoolean(
                KEY_ENABLED,
                enabled
            )
            .apply()
    }

    fun saveTime(
        hour: Int,
        minute: Int
    ) {
        prefs.edit()
            .putInt(
                KEY_HOUR,
                hour
            )
            .putInt(
                KEY_MINUTE,
                minute
            )
            .apply()
    }

    fun getHour(): Int {
        return prefs.getInt(
            KEY_HOUR,
            20
        )
    }

    fun getMinute(): Int {
        return prefs.getInt(
            KEY_MINUTE,
            0
        )
    }
}