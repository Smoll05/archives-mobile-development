package com.android.archives.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (
    tableName = "files",
    foreignKeys = [ForeignKey(
        entity = FolderItem::class,
        parentColumns = ["folderId"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["folderId"])]
)
data class File (
    @PrimaryKey(autoGenerate = true)
    val fileId: Long = 0L,
    val fileName: String,
    val fileType: String,
    val filePath: String,
    var folderId: Long = 0L
)