package com.android.archives.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.archives.data.dao.FileDao
import com.android.archives.data.dao.FolderDao
import com.android.archives.data.dao.ScheduleDao
import com.android.archives.data.dao.TaskDao
import com.android.archives.data.dao.UserDao
import com.android.archives.data.model.File
import com.android.archives.data.model.FolderItem
import com.android.archives.data.model.Schedule
import com.android.archives.data.model.Task
import com.android.archives.data.model.User

@Database(
    entities = [
        User::class,
        Schedule::class,
        Task::class,
        FolderItem::class,
        File::class
    ],
    version = 3
)
@TypeConverters(Converters::class)
abstract class ArchivesDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val taskDao: TaskDao
    abstract val scheduleDao: ScheduleDao
    abstract val folderDao: FolderDao
    abstract val fileDao: FileDao
}