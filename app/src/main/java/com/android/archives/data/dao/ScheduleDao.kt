package com.android.archives.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.android.archives.data.model.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Upsert
    suspend fun upsertSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedules WHERE userId = :userId")
    fun getSchedules(userId: Long): Flow<List<Schedule>>

}