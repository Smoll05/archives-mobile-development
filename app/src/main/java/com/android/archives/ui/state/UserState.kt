package com.android.archives.ui.state

data class UserState (
    val userId: Long = -1L,
    val username: String = "",
    val password: String = "",
    val fullName: String = "",
    val birthday: Long = 0L,
    val program: String = "",
    val school: String = "",
    val pictureFilePath: String? = null,
    val isAddingUser: Boolean = false
)