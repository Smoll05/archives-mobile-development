package com.android.archives.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArchivesDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val taskDao: TaskDao
    abstract val scheduleDao: ScheduleDao

    companion object {
        @Volatile
        private var instance: ArchivesDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context : Context) =
            Room.databaseBuilder (
                context.applicationContext,
                ArchivesDatabase::class.java,
                "article_db.db"
            ).build()
    }
}