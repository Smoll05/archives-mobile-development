package com.android.archives.data.model

import java.util.Calendar

data class Schedule (
    val id: Long,
    val title: String,
    val location: String,
    val color: Int,
    val startTime: Calendar,
    val endTime: Calendar,
)