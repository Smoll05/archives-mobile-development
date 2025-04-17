package com.android.archives.ui.state

import com.android.archives.data.model.Task

data class TaskState (
    val currentTask: Task? = null,
    val todoTask: List<Task> = emptyList(),
    val completeTask: List<Task> = emptyList(),

    val title: String = "",
    val description: String = "",
    val emojiIcon: String = "",
    val isComplete: Boolean = false,

    val isLoading: Boolean = false
)