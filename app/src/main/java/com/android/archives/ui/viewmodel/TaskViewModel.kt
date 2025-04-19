package com.android.archives.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.TaskDao
import com.android.archives.data.model.Task
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.state.TaskState
import com.android.archives.utils.SharedPrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor (
    private val sharedPrefs: SharedPrefsHelper,
    private val dao: TaskDao
): ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getTasks(
                sharedPrefs.getCurrentUser()
            ).collectLatest { tasks ->
                val (todo, complete) = tasks.partition { !it.isComplete }
                _state.update {
                    it.copy(
                        todoTask = todo,
                        completeTask = complete,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    dao.deleteTask(event.task)
                }
            }
            is TaskEvent.EditTask -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        isLoading = true
                    ) }

                    val task = event.task
                    val current = state.value

                    task.title = current.title
                    task.description = current.description
                    task.emojiIcon = current.emojiIcon

                    dao.upsertTask(task)
                }
            }
            is TaskEvent.LoadTask -> {
                val task = event.task

                _state.update { it.copy(
                    currentTask = task,
                    title = task.title,
                    description = task.description,
                    emojiIcon = task.emojiIcon,
                ) }
            }
            TaskEvent.SaveTask -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        isLoading = true
                    ) }

                    val current = _state.value
                    val (title, description, emoji) = Triple(
                        current.title, current.description, current.emojiIcon
                    )

                    val task = Task(
                        title = title,
                        description = description,
                        emojiIcon = emoji,
                        isComplete = false,
                        userId = sharedPrefs.getCurrentUser()
                    )

                    dao.upsertTask(task)
                }
            }
            is TaskEvent.SetCompletion -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }

                    val task = event.task
                    task.isComplete = event.isComplete
                    dao.upsertTask(task)
                }
            }
            is TaskEvent.SetDescription -> {
                _state.update { it.copy(
                    description = event.description
                ) }
            }
            is TaskEvent.SetEmoji -> {
                _state.update { it.copy(
                    emojiIcon = event.emojiIcon
                ) }
            }
            is TaskEvent.SetTitle -> {
                _state.update { it.copy(
                    title = event.title
                ) }
            }
        }
    }
}