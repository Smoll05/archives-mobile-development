package com.android.archives.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.android.archives.data.model.File
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Upsert
    suspend fun upsertFile(file: File) : Long

    @Delete
    suspend fun deleteFile(file: File)

    @Query("SELECT * FROM files WHERE folderId = :folderId")
    fun getFiles(folderId: Long): Flow<List<File>>
}