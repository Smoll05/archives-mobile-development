package com.android.archives.ui.event

import com.android.archives.data.model.Task

sealed interface TaskEvent {
    data object LoadTasks : TaskEvent

    data class SetTitle(val title: String) : TaskEvent
    data class SetDescription(val description: String) : TaskEvent
    data class SetEmoji(val emojiIcon: String) : TaskEvent
    data class SetCompletion(val isComplete: Boolean) : TaskEvent

    data object SaveTask : TaskEvent
    data class EditTask(val task: Task) : TaskEvent
    data class DeleteTask(val task: Task) : TaskEvent
}