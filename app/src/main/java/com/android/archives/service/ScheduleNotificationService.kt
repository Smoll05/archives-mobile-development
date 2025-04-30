package com.android.archives.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android.archives.R
import com.android.archives.ui.activity.MainActivity

class ScheduleNotificationService(
    private val context: Context
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(description: String) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            SCHEDULE_NOTIFICATION_ID,
            activityIntent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(context, SCHEDULE_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Scheduled event starting in 10 minutes")
            .setContentText(description)
            .setContentIntent(activityPendingIntent)
            .build()

        notificationManager.notify(
            SCHEDULE_NOTIFICATION_ID,
            notification

        )
    }

    fun scheduleNotification(context: Context, triggerAtMillis: Long, description: String) {
        val intent = Intent(context, ScheduleNotificationService::class.java).apply {
            putExtra("Description", description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    companion object {
        const val SCHEDULE_CHANNEL_ID = "schedule_channel"
        const val SCHEDULE_NOTIFICATION_ID = 1
    }

}