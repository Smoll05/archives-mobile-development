package com.android.archives.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.TaskDao
import com.android.archives.data.model.Task
import com.android.archives.data.service.SharedPrefsService
import com.android.archives.ui.event.TaskEvent
import com.android.archives.ui.state.TaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor (
    private val sharedPrefs: SharedPrefsService,
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

                    val current = state.value
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
                _state.update { it.copy(
                    isComplete = event.isComplete
                ) }
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
            is TaskEvent.SetTaskCompletion -> {
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

            TaskEvent.DeleteAllTask -> {
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }
                    state.value.todoTask.let{ todoTask ->
                        todoTask.forEach { task ->
                            dao.deleteTask(task)
                        }
                    }

                    state.value.completeTask.let{ completeTask ->
                        completeTask.forEach { task ->
                            dao.deleteTask(task)
                        }
                    }
                    _state.update { it.copy(isLoading = false) }
                }
            }

            is TaskEvent.EditTask -> {
                viewModelScope.launch {
                    val updatedTask = event.task.copy(
                        title = state.value.title,
                        description = state.value.description,
                        emojiIcon = state.value.emojiIcon
                    )

                    Log.d("TaskAdded", "Updated task: $updatedTask")

                    _state.update {
                        it.copy(
                            currentTask = updatedTask,
                            isLoading = true
                        )
                    }
                    dao.upsertTask(updatedTask)
                }
            }
        }
    }
}