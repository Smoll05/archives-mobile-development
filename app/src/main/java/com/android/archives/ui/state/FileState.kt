package com.android.archives.ui.state

import com.android.archives.data.model.File

data class FileState (
    val fileList: List<File> = emptyList(),
    val fileName: String = "",
    val fileType: String = "",
    val filePath: String = "",
    val folderId: Long = 0L,
    var isLoading: Boolean = false
)