package com.android.archives.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.archives.data.dao.FolderDao
import com.android.archives.ui.event.FolderEvent
import com.android.archives.ui.state.FolderState
import com.android.archives.utils.SharedPrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefsHelper,
    private val dao: FolderDao
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
}