package com.android.archives.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.archives.data.dao.ScheduleDao
import com.android.archives.data.dao.TaskDao
import com.android.archives.data.dao.UserDao
import com.android.archives.data.model.Schedule
import com.android.archives.data.model.Task
import com.android.archives.data.model.User

@Database(
    entities = [
        User::class,
        Schedule::class,
        Task::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class ArchivesDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val taskDao: TaskDao
    abstract val scheduleDao: ScheduleDao
}