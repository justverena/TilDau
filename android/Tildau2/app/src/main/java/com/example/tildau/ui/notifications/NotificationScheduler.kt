package com.example.tildau.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    private const val REQUEST_CODE = 100

    fun schedule(
        context: Context,
        hour: Int,
        minute: Int
    ) {

        android.util.Log.d(
            "NOTIFICATION_DEBUG",
            "schedule() called"
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent =
            Intent(
                context,
                NotificationReceiver::class.java
            )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val calendar =
            Calendar.getInstance().apply {

                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

        android.util.Log.d(
            "NOTIFICATION_DEBUG",
            "Alarm time = ${calendar.time}"
        )

        android.util.Log.d(
            "NOTIFICATION_DEBUG",
            "Registering alarm"
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancel(
        context: Context
    ) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent =
            Intent(
                context,
                NotificationReceiver::class.java
            )

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        alarmManager.cancel(pendingIntent)
    }
}