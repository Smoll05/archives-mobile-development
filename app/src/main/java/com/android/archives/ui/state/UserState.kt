package com.android.archives.ui.state

import com.android.archives.data.model.User

data class UserState (
    val currentUser: User? = null,
    val username: String = "",
    val password: String = "",
    val fullName: String = "",
    val birthday: Long = 0L,
    val program: String = "",
    val school: String = "",
    val pictureFilePath: String? = null,
    val isAddingUser: Boolean = false,
)