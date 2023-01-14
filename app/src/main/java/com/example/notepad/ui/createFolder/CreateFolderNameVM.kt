package com.example.notepad.ui.createFolder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notepad.data.model.Folder
import com.example.notepad.data.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFolderNameVM @Inject constructor(
    private val folderRepository: FolderRepository
): ViewModel() {

    fun createFolder(name: String, result: () -> Unit) {
        viewModelScope.launch {
            folderRepository.insert(Folder(title = name))
            result()
        }
    }
}