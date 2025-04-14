package com.android.archives.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.UserDao
import com.android.archives.data.event.UserEvent
import com.android.archives.data.model.User
import com.android.archives.ui.state.UserState
import com.android.archives.utils.SharedPrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor (
    private val sharedPrefs: SharedPrefsHelper,
    private val dao: UserDao
) : ViewModel() {

    private val TAG = "UserViewModel"

//    private val _userId = MutableStateFlow(sharedPrefs.getCurrentUser())

//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val _user = _userId.flatMapLatest { userId ->
//        if (userId != -1L) {
//            dao.getUserWithId(userId)
//        } else {
//            flowOf(null)
//        }
//    }

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

//    val state = combine(_state, _user) { state, user ->
//        state.copy(
//            userId = user?.userId ?: -1L,
//            username = user?.username ?: "",
//            password = user?.password ?: "",
//            fullName = user?.fullName ?: "",
//            birthday = user?.birthday ?: 0L,
//            program = user?.program ?: "",
//            school = user?.school ?: "",
//            pictureFilePath = user?.pictureFilePath,
//            isAddingUser = false
//        )
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UserState())

//    private val _currentUserId = MutableStateFlow(
//        sharedPrefs.getCurrentUser().takeIf { it != -1L }
//    )

//    private val _user = _currentUserId
//        .flatMapLatest { userId ->
//            userId?.let {
//                dao.getUserWithId(it)
//            } ?: flowOf(null)
//        }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

//    private val _user = MutableStateFlow(
//        dao.getUserWithId(sharedPrefs.getCurrentUser())
//    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
//
//    val state = combine(_state, _user) { state, user ->
//        state.copy(
//            user = user
//        )
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserState())

    init {
        Log.d(TAG, "User View Model Created")
    }

    fun onEvent(event: UserEvent) {
        when(event) {
            is UserEvent.DeleteUser -> {
                viewModelScope.launch {
                    dao.deleteUser(event.user)
                }
            }
            UserEvent.HideForm -> {
                _state.update { it.copy(
                    isAddingUser = false
                ) }
            }
            UserEvent.SaveUser -> {
                val username = state.value.username
                val password = state.value.password
                val fullName = state.value.fullName
                val birthday = state.value.birthday
                val program = state.value.program
                val school = state.value.school
                val pictureFilePath = state.value.pictureFilePath

                if(username.isBlank() || password.isBlank() || fullName.isBlank() ||
                    birthday == 0L || program.isBlank() || school.isBlank()) {

                    Log.d(TAG, "Filled Blank")

                    Log.d(TAG, "Username: $username")
                    Log.d(TAG, "Password: $password")
                    Log.d(TAG, "Full Name: $fullName")
                    Log.d(TAG, "Birthday: $birthday")
                    Log.d(TAG, "Program: $program")
                    Log.d(TAG, "School: $school")
                    return
                }

                val user = User(
                    username = username,
                    password = password,
                    fullName = fullName,
                    birthday = birthday,
                    program = program,
                    school = school,
                    pictureFilePath = pictureFilePath
                )

                viewModelScope.launch {
                    val userId = dao.upsertUser(user)
                    sharedPrefs.setCurrentUser(userId)
                }

                emptyCurrentState()
            }
            UserEvent.LoadUser -> {
                val userId = sharedPrefs.getCurrentUser()
                if(userId == -1L) return

                viewModelScope.launch {
                    dao.getUserWithId(userId).collectLatest { user ->
                        _state.update { state ->
                            state.copy(
                                userId = user.userId,
                                username = user.username,
                                password = user.password,
                                fullName = user.fullName,
                                birthday = user.birthday,
                                program = user.program,
                                school = user.school,
                                pictureFilePath = user.pictureFilePath,
                                isAddingUser = false
                            )
                        }
                    }
                }
            }
            is UserEvent.SetBirthday -> {
                _state.update { it.copy(
                    birthday = event.birthday
                ) }
            }
            is UserEvent.SetFullName -> {
                _state.update { it.copy(
                    fullName = event.fullName
                ) }
            }
            is UserEvent.SetPassword -> {
                _state.update { it.copy(
                    password = event.password
                ) }


                Log.d(TAG, "Password: ${state.value.password}")
            }
            is UserEvent.SetPictureFilePath -> {
                _state.update { it.copy(
                    pictureFilePath = event.pictureFilePath
                ) }
            }
            is UserEvent.SetProgram -> {
                _state.update { it.copy(
                    program = event.program
                ) }
            }
            is UserEvent.SetSchool -> {
                _state.update { it.copy(
                    school = event.school
                ) }
            }
            is UserEvent.SetUserName -> {
                _state.update { it.copy(
                    username = event.username
                ) }
            }
            UserEvent.ShowForm -> {
                _state.update { it.copy(
                    isAddingUser = true
                ) }
            }
        }
    }

    private fun emptyCurrentState() {
        _state.update { it.copy(
            isAddingUser = false,
            username = "",
            password = "",
            fullName = "",
            birthday = 0L,
            program = "",
            school = "",
            pictureFilePath = null
        ) }
    }
}