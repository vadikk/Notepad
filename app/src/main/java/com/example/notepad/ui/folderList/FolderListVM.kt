package com.example.notepad.ui.folderList

import androidx.lifecycle.viewModelScope
import com.example.notepad.data.model.Folder
import com.example.notepad.data.model.Note
import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.domain.repository.NoteRepository
import com.example.notepad.ui.presentation.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
            val notes = async { noteRepository.notes() }.await()
            folderRepository.getFolders().collect {
                folderList = it
                fillFolderList(it, notes)
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

    private fun fillFolderList(folders: List<Folder>, notes: List<Note>) {
        val isSelectAllNotes = folders.none { it.isSelected }

        val newFolders = mutableListOf<FolderEntity>().apply {
            add(FolderEntity.FolderIdle)
            add(
                FolderEntity.FolderItem(
                    Folder(
                        uid = -1,
                        title = "All notes",
                        isSelected = isSelectAllNotes,
                        countNote = notes.size
                    )
                )
            )
        }
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