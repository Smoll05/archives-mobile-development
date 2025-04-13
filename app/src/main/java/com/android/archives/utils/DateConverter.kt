package com.android.archives.utils

import java.text.SimpleDateFormat
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
    }
}