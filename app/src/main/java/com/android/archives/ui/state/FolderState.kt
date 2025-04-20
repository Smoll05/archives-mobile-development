package com.android.archives.ui.state

import com.android.archives.data.model.FolderItem

data class FolderState (
    val folderList: List<FolderItem> = emptyList(),
    val title: String = "",
    val name: String = "",
    val iconRes: Int = 0,
)