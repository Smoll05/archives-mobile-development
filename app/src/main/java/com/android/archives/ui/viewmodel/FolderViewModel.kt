package com.android.archives.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.FileDao
import com.android.archives.data.dao.FolderDao
import com.android.archives.data.service.SharedPrefsService
import com.android.archives.ui.event.FolderEvent
import com.android.archives.ui.state.FolderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefsService,
    private val dao: FolderDao,
    private val fileDao: FileDao
) : ViewModel() {
    private val _state = MutableStateFlow(FolderState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getFolders(
                sharedPrefs.getCurrentUser()
            ).collectLatest { folders ->
                _state.update {
                    it.copy(
                        folderList = folders,
                    )
                }
            }
        }
    }

    fun onEvent(event: FolderEvent) {
        when(event) {
            is FolderEvent.DeleteFolder -> {
                viewModelScope.launch {
                    dao.deleteFolder(event.folder)
                }
            }
            is FolderEvent.SaveFolder -> {
                viewModelScope.launch {
                    val folder = event.folder

                    folder.userId = sharedPrefs.getCurrentUser()
                    dao.upsertFolder(folder)
                }
            }
            is FolderEvent.SetIconType -> {
                _state.update { it.copy(
                    iconRes = event.iconType
                )}
            }
            is FolderEvent.SetName -> {
                _state.update { it.copy(
                    name = event.name
                )}
            }
        }
    }

    fun deleteAllFilesFromFolders() {
        viewModelScope.launch(Dispatchers.IO) {
            val folderList = dao.getFolders(sharedPrefs.getCurrentUser()).first()
            Log.d("FileDeletion", folderList.toString())

            folderList.forEach { fi->
                val fileList = fileDao.getFiles(fi.folderId).first()

                fileList.forEach { fileItem ->
                    val file = java.io.File(fileItem.filePath)
                    if (file.exists()) {
                        val deleted = file.delete()
                        Log.d("FileDeletion", "Deleted ${file.absolutePath}: $deleted")
                    } else {
                        Log.d("FileDeletion", "File not found: ${file.absolutePath}")
                    }
                }
                dao.deleteFolder(fi)
            }
        }
    }
}