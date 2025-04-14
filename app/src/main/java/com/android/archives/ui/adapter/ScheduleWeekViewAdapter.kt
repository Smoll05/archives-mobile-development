package com.android.archives.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import com.android.archives.R
import com.android.archives.data.model.Schedule
import com.android.archives.ui.viewmodel.ScheduleViewModel

class ScheduleWeekViewAdapter(
    private val viewModel: ScheduleViewModel,
    private val lifecycleOwner: LifecycleOwner
) : WeekView.SimpleAdapter<Schedule>() {
    private var currentEvents: List<Schedule> = emptyList()

    init {
        // Observe the ViewModel's events
        viewModel.events.observe(lifecycleOwner) { newEvents ->
            submitList(newEvents)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateEntity(item: Schedule): WeekViewEntity {
        val backgroundColor = getScheduleColor(context, item.color)
        val textColor = Color.BLACK

        val titleSpan = TypefaceSpan(ResourcesCompat.getFont(context, R.font.inter)?.let {
            Typeface.create(it, Typeface.NORMAL)
        } ?: Typeface.DEFAULT)

        val style = WeekViewEntity.Style.Builder()
            .setTextColor(textColor)
            .setBackgroundColor(backgroundColor)
            .build()

        val title = SpannableStringBuilder(item.title).apply {
            setSpan(titleSpan, 0, item.title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val subtitle = SpannableStringBuilder(item.location).apply {
            val subtitleSpan = RelativeSizeSpan(0.8f) // 0.8f makes it 80% of the normal size
            setSpan(subtitleSpan, 0, item.location?.length ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }


        return WeekViewEntity.Event.Builder(item)
            .setId(item.scheduleId)
            .setTitle(title)
            .setStartTime(item.startTime)
            .setEndTime(item.endTime)
            .setSubtitle(subtitle)
            .setStyle(style)
            .build()
    }

    override fun onEventClick(data: Schedule, bounds: RectF) {
        Log.d("CalendarListener", "You have clicked {${data.title}")
    }

//    override fun onLoadMore(startDate: LocalDate, endDate: LocalDate): List<CalendarEvent> {
//        // Return only events within the visible date range
//        return currentEvents.filter {
//            val eventStart = it.startTime.toLocalDate()
//            eventStart.isAfter(startDate.minusDays(1)) && eventStart.isBefore(endDate.plusDays(1))
//        }
//    }

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
}