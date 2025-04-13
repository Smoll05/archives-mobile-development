package com.android.archives.data.dao

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.android.archives.data.model.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleDao {

    @Upsert
    suspend fun upsertSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedules WHERE userId = :userId")
    fun getTasks(userId: Long): Flow<List<Schedule>>

}