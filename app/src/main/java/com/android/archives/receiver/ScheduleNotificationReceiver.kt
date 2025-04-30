package com.android.archives.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.archives.service.ScheduleNotificationService

class ScheduleNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val description = intent.getStringExtra("description")?: "Scheduled event"
        val service = ScheduleNotificationService(context)
        service.showNotification(description)
    }
}