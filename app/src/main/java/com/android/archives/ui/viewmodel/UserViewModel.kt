package com.android.archives.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.UserDao
import com.android.archives.data.model.User
import com.android.archives.ui.event.UserEvent
import com.android.archives.ui.state.UserState
import com.android.archives.utils.PasswordEncryptor
import com.android.archives.utils.SharedPrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    fun onEvent(event: UserEvent) {
        when(event) {
            is UserEvent.DeleteUser -> {
                viewModelScope.launch {
                    _state.value.currentUser?.let { dao.deleteUser(it) }
                }
            }
            UserEvent.HideForm -> {
                _state.update { it.copy(
                    isAddingUser = false
                ) }
            }
            UserEvent.LoadUser -> {
                val userId = sharedPrefs.getCurrentUser()
                if(userId == 0L) return

                viewModelScope.launch {
                    dao.getUserWithId(userId).collectLatest { user ->

                        if (user == null) {
                            return@collectLatest
                        }

                        _state.update { state ->
                            state.copy(
                                currentUser = user
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

    suspend fun registerUser() : Boolean {
        val current = state.value

        val username = current.username
        val password = PasswordEncryptor.hashPassword(current.password)
        val fullName = current.fullName
        val birthday = current.birthday
        val program = current.program
        val school = current.school
        val pictureFilePath = current.pictureFilePath

        if(username.isBlank() || password.isBlank() || fullName.isBlank() ||
            birthday == 0L || program.isBlank() || school.isBlank()) {
            return false
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

        val userId = dao.upsertUser(user)
        sharedPrefs.setCurrentUser(userId)

        emptyCurrentState()
        return true
    }

    suspend fun updateUser() : Boolean {
        val current = state.value

        val userId = current.currentUser?.userId
        val username = current.username
        val password = PasswordEncryptor.hashPassword(current.password)
        val fullName = current.fullName
        val birthday = current.birthday
        val program = current.program
        val school = current.school
        val pictureFilePath = current.pictureFilePath

        if(username.isBlank() || password.isBlank() || fullName.isBlank() ||
            birthday == 0L || program.isBlank() || school.isBlank()) {
            return false
        }

        val user = userId?.let { id ->
            User(
                userId = id,
                username = username,
                password = password,
                fullName = fullName,
                birthday = birthday,
                program = program,
                school = school,
                pictureFilePath = pictureFilePath
            )
        }

        viewModelScope.launch {
            if (user != null) {
                _state.update { it.copy(
                    currentUser = user
                )}
                dao.upsertUser(user)
            }
        }

        return true
    }

    suspend fun getUserWithUsernameAndPassword(username: String, password: String) : Boolean {
        val user = dao.getUserWithUsernameAndPassword(
            username, PasswordEncryptor.hashPassword(password)
        )

        return if (user != null) {
            sharedPrefs.setCurrentUser(user.userId)
            true
        } else {
            false
        }
    }

    fun loadStateFromCurrentUser() {
        val user = state.value.currentUser ?: return

        _state.update { state ->
            state.copy(
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

    private fun emptyCurrentState() {
        _state.update { it.copy(
            username = "",
            password = "",
            fullName = "",
            birthday = 0L,
            program = "",
            school = "",
            pictureFilePath = null,
            isAddingUser = false,
        ) }
    }

    fun getUserById(userId: Long): Flow<User> {
        return dao.getUserWithId(userId)
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            dao.upsertUser(user)
        }
    }

}