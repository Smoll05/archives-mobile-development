package com.android.archives.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.android.archives.data.model.FolderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Upsert
    suspend fun upsertFolder(folder: FolderItem) : Long

    @Delete
    suspend fun deleteFolder(folder: FolderItem)

    @Query("SELECT * FROM folders WHERE userId = :userId")
    fun getFolders(userId: Long): Flow<List<FolderItem>>
}