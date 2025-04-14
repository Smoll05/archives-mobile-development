package com.android.archives.data.event

import com.android.archives.data.model.User

sealed interface UserEvent {
    data object SaveUser: UserEvent
    data object LoadUser: UserEvent
    data class SetUserName(val username: String): UserEvent
    data class SetPassword(val password: String): UserEvent
    data class SetFullName(val fullName: String): UserEvent
    data class SetBirthday(val birthday: Long): UserEvent
    data class SetProgram(val program: String): UserEvent
    data class SetSchool(val school: String): UserEvent
    data class SetPictureFilePath(val pictureFilePath: String): UserEvent
    data object ShowForm: UserEvent
    data object HideForm: UserEvent
    data class DeleteUser(val user: User): UserEvent
}