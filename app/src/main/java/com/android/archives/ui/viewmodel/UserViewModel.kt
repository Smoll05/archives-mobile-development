package com.android.archives.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.UserDao
import com.android.archives.data.event.UserEvent
import com.android.archives.data.model.User
import com.android.archives.ui.state.UserState
import com.android.archives.utils.SharedPrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor (
    private val sharedPrefs: SharedPrefsHelper,
    private val dao: UserDao
) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
//    private val _user = dao.getUserWithId(SharedPrefsHelper.UserSession.getCurrentUser())

    val state : StateFlow<UserState> = _state

    class UserViewModelProviderFactory(
        private val userDao: UserDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
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
            is UserEvent.SaveUser -> {
                val context = event.context

                val username = state.value.username
                val password = state.value.password
                val fullName = state.value.fullName
                val birthday = state.value.birthday
                val program = state.value.program
                val school = state.value.school
                val pictureFilePath = state.value.pictureFilePath

                if(username.isBlank() || password.isBlank() || fullName.isBlank() ||
                    birthday == 0L || program.isBlank() || school.isBlank()) {
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
                    SharedPrefsHelper.UserSession.setCurrentUser(context, userId)
                }
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
}