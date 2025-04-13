package com.android.archives.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0L,
    val username: String,
    val password: String,
    var fullName: String,
    var birthday: Long,
    var program: String,
    var school: String,
    var pictureFilePath: String? = null
)