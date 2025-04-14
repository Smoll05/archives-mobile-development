package com.android.archives.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "schedules",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
)
data class Schedule (
    @PrimaryKey(autoGenerate = true)
    val scheduleId: Long = 0L,
    var title: String,
    var location: String? = null,
    var color: Int,
    var startTime: Calendar,
    var endTime: Calendar,
    val userId: Long = 0L
)