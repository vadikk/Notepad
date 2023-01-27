package com.example.notepad.ui.createFolder

import androidx.lifecycle.viewModelScope
import com.example.notepad.data.model.Folder
import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.ui.presentation.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFolderNameVM @Inject constructor(
    private val folderRepository: FolderRepository
) : BaseVM<CreateFolderState, Nothing, CreateFolderEffect>() {

    override fun createInitialState(): CreateFolderState = CreateFolderState.Idle

    override fun handleEvents(event: Nothing) = Unit

    fun createFolder(name: String) {
        viewModelScope.launch {
            folderRepository.insert(Folder(title = name))
            setEffect { CreateFolderEffect.CloseScreen }
        }
    }
}