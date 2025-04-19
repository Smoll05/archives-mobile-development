package com.android.archives.constants

import android.content.Context
import androidx.core.content.ContextCompat
import com.android.archives.R

enum class ScheduleColorType(val colorResId: Int) {
    SCHEDULE_WHITE(R.color.white),
    SCHEDULE_YELLOW(R.color.yellow),
    SCHEDULE_ORANGE(R.color.orange),
    SCHEDULE_RED(R.color.red),
    SCHEDULE_PURPLE(R.color.purple),
    SCHEDULE_BLUE(R.color.blue),
    SCHEDULE_GREEN(R.color.green)
}

private fun getScheduleColor(context: Context, colorInt: Int): Int {
    return when (colorInt) {
        1 -> ContextCompat.getColor(context, R.color.white)
        2 -> ContextCompat.getColor(context, R.color.yellow)
        3 -> ContextCompat.getColor(context, R.color.orange)
        4 -> ContextCompat.getColor(context, R.color.red)
        5 -> ContextCompat.getColor(context, R.color.purple)
        6 -> ContextCompat.getColor(context, R.color.blue)
        7 -> ContextCompat.getColor(context, R.color.green)
        else -> ContextCompat.getColor(context, R.color.white)
    }
}