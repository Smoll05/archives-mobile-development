package com.android.archives.ui.event

import com.android.archives.data.model.File

sealed interface FileEvent {
    data class SaveFile(val file: File) : FileEvent
    data class DeleteFile(val file: File) : FileEvent
    data class SetFileName(val name: String) : FileEvent
    data class SetFileType(val type: String) : FileEvent
    data class SetFilePath(val path: String) : FileEvent
    data class SetFolderId(val folderId: Long) : FileEvent
}