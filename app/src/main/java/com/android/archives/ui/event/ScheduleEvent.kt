package com.android.archives.ui.event

import com.android.archives.constants.ScheduleColorType
import com.android.archives.data.model.Schedule

sealed interface ScheduleEvent {
    data class LoadSchedule(val schedule: Schedule) : ScheduleEvent
    data object SaveSchedule : ScheduleEvent
    data class EditSchedule(val schedule: Schedule): ScheduleEvent
    data class DeleteSchedule(val schedule: Schedule) : ScheduleEvent
    data class SetTitle(val title: String) : ScheduleEvent
    data class SetLocation(val location: String) : ScheduleEvent
    data class SetDate(val dateInMillis: Long) : ScheduleEvent
    data class SetColorType(val colorType: ScheduleColorType) : ScheduleEvent
    data class SetTimeStartHour(val startHour: Int) : ScheduleEvent
    data class SetTimeStartMin(val startMin: Int) : ScheduleEvent
    data class SetTimeEndHour(val endHour: Int) : ScheduleEvent
    data class SetTimeEndMin(val endMin: Int) : ScheduleEvent
}