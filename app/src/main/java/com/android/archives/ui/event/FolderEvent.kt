package com.android.archives.ui.event

import com.android.archives.data.model.FolderItem

sealed interface FolderEvent {
    data class SaveFolder(val folder: FolderItem) : FolderEvent
    data class DeleteFolder(val folder: FolderItem) : FolderEvent
    data class SetName(val name: String) : FolderEvent
    data class SetIconType(val iconType: Int) : FolderEvent
}