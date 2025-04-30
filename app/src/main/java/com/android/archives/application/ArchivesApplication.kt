package com.android.archives.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.android.archives.service.ScheduleNotificationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ArchivesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ScheduleNotificationService.SCHEDULE_CHANNEL_ID,
                "Schedule",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Used to notify upcoming schedules"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}