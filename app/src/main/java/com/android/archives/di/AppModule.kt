package com.android.archives.di

import android.content.Context
import androidx.room.Room
import com.android.archives.data.dao.UserDao
import com.android.archives.data.db.ArchivesDatabase
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao() : UserDao {

    }
}