package com.android.archives.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.ScheduleDao
import com.android.archives.data.model.Schedule
import com.android.archives.data.service.SharedPrefsService
import com.android.archives.ui.event.ScheduleEvent
import com.android.archives.ui.state.ScheduleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor (
    private val sharedPrefs: SharedPrefsService,
    private val dao: ScheduleDao,
) : ViewModel() {
    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getSchedules(
                sharedPrefs.getCurrentUser()
            ).collectLatest { schedules ->
                _state.update {
                    it.copy(
                        scheduleList = schedules,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: ScheduleEvent) {
        when(event) {
            is ScheduleEvent.DeleteSchedule -> {
                viewModelScope.launch {
                    dao.deleteSchedule(event.schedule)
                }
            }
            is ScheduleEvent.EditSchedule -> {
                viewModelScope.launch {
                    val schedule = event.schedule
                    val current = state.value

                    val updatedSchedule = schedule.copy(
                        title = current.title,
                        location = current.location,
                        colorType = current.colorType,
                        date = current.date,
                        startTimeHour = current.startHour,
                        startTimeMin = current.startMin,
                        endTimeHour = current.endHour,
                        endTimeMin = current.endMin
                    )

                    dao.upsertSchedule(updatedSchedule)
                }
            }
            ScheduleEvent.SaveSchedule -> {
                val current = state.value

                val schedule = Schedule(
                    title = current.title,
                    location = current.location,
                    colorType = current.colorType,
                    date = current.date,
                    startTimeHour = current.startHour,
                    startTimeMin = current.startMin,
                    endTimeHour = current.endHour,
                    endTimeMin = current.endMin,
                    userId = sharedPrefs.getCurrentUser()
                )

                viewModelScope.launch {
                    dao.upsertSchedule(schedule)
                }
            }
            is ScheduleEvent.SetDate -> {
                _state.update { it.copy(
                    date = event.dateInMillis
                )}
            }
            is ScheduleEvent.SetLocation -> {
                _state.update { it.copy(
                    location = event.location
                )}
            }
            is ScheduleEvent.SetTimeEndHour -> {
                _state.update { it.copy(
                    endHour = event.endHour
                )}
            }
            is ScheduleEvent.SetTimeEndMin -> {
                _state.update { it.copy(
                    endMin = event.endMin
                )}
            }
            is ScheduleEvent.SetTimeStartHour -> {
                _state.update { it.copy(
                    startHour = event.startHour
                )}
            }
            is ScheduleEvent.SetTimeStartMin -> {
                _state.update { it.copy(
                    startMin = event.startMin
                )}
            }
            is ScheduleEvent.SetTitle -> {
                _state.update { it.copy(
                    title = event.title
                )}
            }
            is ScheduleEvent.SetColorType -> {
                _state.update { it.copy(
                    colorType = event.colorType
                )}
            }

            ScheduleEvent.DeleteAllSchedule -> {
                viewModelScope.launch {
                    state.value.scheduleList.let{ scheduleList ->
                        scheduleList.forEach { schedule ->
                            dao.deleteSchedule(schedule)
                        }
                    }
                }
            }
        }
    }
}

