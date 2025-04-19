package com.android.archives.data.db

import androidx.room.TypeConverter
import com.android.archives.constants.ScheduleColorType
import java.util.Calendar

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let {
            Calendar.getInstance().apply { timeInMillis = it }
        }
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun fromScheduleColorType(type: ScheduleColorType): Int {
        return type.ordinal + 1
    }

    @TypeConverter
    fun toScheduleColorType(value: Int): ScheduleColorType {
        return ScheduleColorType.entries.getOrNull(value - 1) ?: ScheduleColorType.SCHEDULE_WHITE
    }
}