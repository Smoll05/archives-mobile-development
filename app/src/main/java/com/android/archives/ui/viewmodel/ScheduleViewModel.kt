package com.android.archives.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.archives.data.model.Schedule
import java.util.Calendar

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val _events = MutableLiveData<List<Schedule>>()
    val events: LiveData<List<Schedule>> = _events

    init {
        loadEvents()
    }

    private fun loadEvents() {
        _events.value = listOf(
            createCalendarEvent(1, "Morning Meeting", "GLE", 4,2025, Calendar.APRIL, 14, 9, 0, 10, 0),
            createCalendarEvent(2, "Lunch Break", "NGE", 5, 2025, Calendar.APRIL, 14, 12, 30, 13, 30),
            createCalendarEvent(3, "Design Review", "Malabuyoc", 0, 2025, Calendar.APRIL, 15, 14, 0, 15, 0)
        )
    }

    fun createCalendarEvent(
        id: Long,
        title: String,
        location: String,
        color: Int,
        year: Int,
        month: Int,
        day: Int,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ): Schedule {
        val start = Calendar.getInstance().apply {
            set(year, month, day, startHour, startMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val end = Calendar.getInstance().apply {
            set(year, month, day, endHour, endMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return Schedule(id, title, location, color, start, end)
    }
}

