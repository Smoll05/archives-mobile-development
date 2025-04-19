package com.android.archives.ui.state

import com.android.archives.constants.ScheduleColorType
import com.android.archives.data.model.Schedule

data class ScheduleState (
    val scheduleList: List<Schedule> = emptyList(),
    val title: String = "",
    val location: String = "",
    val colorType: ScheduleColorType = ScheduleColorType.SCHEDULE_WHITE,
    val date: Long = 0L,
    val startHour: Int = 0,
    val startMin: Int = 0,
    val endHour: Int = 0,
    val endMin: Int = 0,
    val isLoading: Boolean = false
)