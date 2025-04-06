package com.android.archives.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.archives.data.model.FolderItem

class MainCourseViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_FOLDERS = "folders"
    }

    fun getFolders(): List<FolderItem> {
        return savedStateHandle.get<List<FolderItem>>(KEY_FOLDERS) ?: emptyList()
    }

    fun addFolder(folder: FolderItem) {
        val updatedList = getFolders().toMutableList().apply { add(folder) }
        savedStateHandle.set(KEY_FOLDERS, updatedList)
    }

    fun setFolders(folders: List<FolderItem>) {
        savedStateHandle.set(KEY_FOLDERS, folders)
    }


}
