package com.example.notepad.ui.folderList

import androidx.lifecycle.viewModelScope
import com.example.notepad.domain.models.Folder
import com.example.notepad.domain.models.Note
import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.domain.repository.NoteRepository
import com.example.notepad.ui.presentation.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderListVM @Inject constructor(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository
) : BaseVM<FoldersState, FoldersEvent, FoldersEffect>() {

    private var folderList = emptyList<Folder>()

    init {
        viewModelScope.launch {
            noteRepository.getNotes().collectLatest { notes ->
                folderRepository.getFolders().collectLatest { folders ->
                    folderList = folders
                    fillFolderList(folders, notes)
                }
            }
        }
    }

    override fun createInitialState(): FoldersState = FoldersState()

    override fun handleEvents(event: FoldersEvent) {
        when (event) {
            is FoldersEvent.SelectFolder -> selectFolder(event.uid)
        }
    }

    private fun selectFolder(uid: Int?) {
        val newFolderList = folderList.map {
            if (it.uid == uid) it.copy(isSelected = true) else it.copy(isSelected = false)
        }

        viewModelScope.launch {
            folderRepository.insertAll(newFolderList)
        }
        setEffect { FoldersEffect.CloseScreen }
    }

    private fun fillFolderList(folderEntities: List<Folder>, noteEntities: List<Note>) {
        val isSelectAllNotes = folderEntities.none { it.isSelected }

        val newFolders = mutableListOf<FolderEntityContract>().apply {
            add(FolderEntityContract.FolderIdle)
            add(
                FolderEntityContract.FolderItem(
                    Folder(
                        uid = -1,
                        title = "All notes",
                        isSelected = isSelectAllNotes,
                        countNote = noteEntities.size
                    )
                )
            )
        }
        newFolders.addAll(
            folderEntities.map { folder ->
                FolderEntityContract.FolderItem(
                    folder.copy(
                        countNote = noteEntities.filter { it.folder == folder.uid.toString() }.size
                    )
                )
            }
        )
        setState { this.copy(folders = newFolders) }
    }
}