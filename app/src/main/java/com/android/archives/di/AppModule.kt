package com.android.archives.di

import android.content.Context
import androidx.room.Room
import com.android.archives.data.dao.ScheduleDao
import com.android.archives.data.dao.TaskDao
import com.android.archives.data.dao.UserDao
import com.android.archives.data.db.ArchivesDatabase
import com.android.archives.utils.SharedPrefsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : ArchivesDatabase {
        return Room.databaseBuilder (
            context.applicationContext,
            ArchivesDatabase::class.java,
            "article_db.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: ArchivesDatabase) : UserDao = database.userDao


    @Provides
    @Singleton
    fun providesTaskDao(database: ArchivesDatabase) : TaskDao = database.taskDao

    @Provides
    @Singleton
    fun provideScheduleDao(database: ArchivesDatabase) : ScheduleDao = database.scheduleDao

    @Provides
    @Singleton
    fun provideSharedPrefsHelper(@ApplicationContext context: Context) : SharedPrefsHelper
        = SharedPrefsHelper(context)
}