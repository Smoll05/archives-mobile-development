package com.android.archives.data.model

data class Task(
    var taskId: Int,
    var title: String,
    var description: String,
    var emojiIcon: String,
    var isComplete: Boolean = false
)