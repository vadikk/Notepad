package com.example.notepad.ui.folderChoose

import androidx.lifecycle.viewModelScope
import com.example.notepad.data.model.Folder
import com.example.notepad.data.model.Note
import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.domain.repository.NoteRepository
import com.example.notepad.ui.folderList.FolderEntity
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
        val newFolders = mutableListOf<FolderEntity>()
        newFolders.addAll(
            folders.map { folder ->
                FolderEntity.FolderItem(
                    folder.copy(
                        countNote = notes.filter { it.folder == folder.uid.toString() }.size
                    )
                )
            }
        )
        setState { this.copy(folderEntities = newFolders) }
    }
}