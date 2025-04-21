package com.android.archives.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.FileDao
import com.android.archives.data.model.File
import com.android.archives.ui.event.FileEvent
import com.android.archives.ui.state.FileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    private val dao: FileDao
): ViewModel() {
    private val _state = MutableStateFlow(FileState())
    val state = _state.asStateFlow()

    private val _folderId = MutableStateFlow<Long?>(null) // Change to Long?
    val folderId = _folderId.asStateFlow()


    init {
        viewModelScope.launch {
            folderId.collectLatest { currentFolderId ->
                if (currentFolderId != null) {
                    dao.getFiles(currentFolderId).collectLatest { files ->
                        _state.update { state -> state.copy(
                            fileList = files,
                        ) }
                    }
                }
            }
        }
    }

    fun onEvent(event: FileEvent) {
        when(event) {
            is FileEvent.DeleteFile -> {
                viewModelScope.launch {
                    dao.deleteFile(event.file)
                }
            }
            is FileEvent.SaveFile -> {
                viewModelScope.launch {
                    dao.upsertFile(event.file)
                }
            }
            is FileEvent.SetFileName -> {
                _state.update { it.copy(
                    fileName = event.name
                )}
            }
            is FileEvent.SetFilePath -> {
                _state.update { it.copy(
                    filePath = event.path
                )}
            }
            is FileEvent.SetFileType -> {
                _state.update { it.copy(
                    fileType = event.type
                )}
            }
            is FileEvent.SetFolderId -> {
                _folderId.value = event.folderId
            }
        }
    }

    fun deleteAllFiles(fileList: List<File>) {
        viewModelScope.launch(Dispatchers.IO) {
            fileList.forEach { fileItem ->
                val file = java.io.File(fileItem.filePath)
                if (file.exists()) {
                    val deleted = file.delete()
                    Log.d("FileDeletion", "Deleted ${file.absolutePath}: $deleted")
                } else {
                    Log.d("FileDeletion", "File not found: ${file.absolutePath}")
                }
            }
        }
    }
}