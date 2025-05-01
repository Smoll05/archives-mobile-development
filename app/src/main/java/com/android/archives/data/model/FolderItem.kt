package com.android.archives.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "folders",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
)
@Parcelize
data class FolderItem(
    @PrimaryKey(autoGenerate = true)
    val folderId: Long = 0L,
    var name: String,
    var iconRes: Int = 0,
    var userId: Long = 0L
) : Parcelable
