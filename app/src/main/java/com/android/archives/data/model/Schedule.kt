package com.android.archives.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.android.archives.constants.ScheduleColorType
import kotlinx.parcelize.Parcelize

@Parcelize
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
    var colorType: ScheduleColorType,
    var date: Long,
    var startTimeHour: Int,
    var startTimeMin: Int,
    var endTimeHour: Int,
    var endTimeMin: Int,
    val userId: Long = 0L
) : Parcelable