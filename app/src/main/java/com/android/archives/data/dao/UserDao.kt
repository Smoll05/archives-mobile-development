package com.android.archives.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.android.archives.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUser(user: User) : Long

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    fun getUserWithUsernameAndPassword(username: String, password: String): Flow<User>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserWithId(userId: Long): Flow<User>
}