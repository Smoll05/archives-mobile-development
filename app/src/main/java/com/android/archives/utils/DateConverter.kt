package com.android.archives.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateConverter {
    companion object {
        fun convertMillisToDateString(millis: Long): String {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            return sdf.format(Date(millis))
        }

        fun convertDateStringToMillis(dateString: String): Long {
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            return try {
                val date: Date? = sdf.parse(dateString)
                date?.time ?: 0L
            } catch (e: Exception) {
                e.printStackTrace()
                0L
            }
        }

        fun convertTimeMillisToTimeString(hour: Int, minute: Int) : String {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }

            val formatter = SimpleDateFormat("h:mma", Locale.getDefault()) // e.g. "9:00AM"
            return formatter.format(calendar.time)
        }

        fun combineDateAndTime(dateInMillis: Long, hour: Int, minute: Int): Calendar {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = dateInMillis

                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar
        }
    }
}