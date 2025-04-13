package com.android.archives.ui.state

import com.android.archives.data.model.User

data class UserState (
    val user: User? = null,
    val username: String = "",
    val password: String = "",
    var fullName: String = "",
    var birthday: Long = 0L,
    var program: String = "",
    var school: String = "",
    var pictureFilePath: String? = null,
    var isAddingUser: Boolean = false
)