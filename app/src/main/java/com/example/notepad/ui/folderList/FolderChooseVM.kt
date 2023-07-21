package com.example.notepad.ui.folderList

import androidx.lifecycle.viewModelScope
import com.example.notepad.domain.models.Folder
import com.example.notepad.domain.models.Note
import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.domain.repository.NoteRepository
import com.example.notepad.ui.presentation.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderChooseVM @Inject constructor(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository
) : BaseVM<FolderListState, Nothing, Nothing>() {

    init {
        viewModelScope.launch {
            val notes = async { noteRepository.notes() }.await()
            folderRepository.getFolders().collect {
                fillFolderList(it, notes)
            }
        }
    }

    override fun createInitialState(): FolderListState = FolderListState()

    override fun handleEvents(event: Nothing) = Unit

    private fun fillFolderList(folders: List<Folder>, notes: List<Note>) {
        val newFolders = mutableListOf<FolderEntityContract>()
        newFolders.addAll(
            folders.map { folder ->
                FolderEntityContract.FolderItem(
                    folder.copy(
                        isSelected = false,
                        countNote = notes.filter { it.folder == folder.uid.toString() }.size
                    )
                )
            }
        )
        setState { this.copy(folders = newFolders) }
    }
}