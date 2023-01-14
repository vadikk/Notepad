package com.example.notepad.ui.folderChoose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notepad.data.model.Folder
import com.example.notepad.data.model.Note
import com.example.notepad.data.repository.FolderRepository
import com.example.notepad.data.repository.NoteRepository
import com.example.notepad.ui.folderList.FolderEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderChooseVM @Inject constructor(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository
): ViewModel(){

    private val _folderEntities = MutableStateFlow<List<FolderEntity>>(emptyList())
    val folderEntities = _folderEntities.asStateFlow()

    init {
        viewModelScope.launch {
            val notes = async { noteRepository.notes() }.await()
            folderRepository.getFolders().collect {
                fillFolderList(it, notes)
            }
        }
    }

    private fun fillFolderList(folders: List<Folder>, notes:List<Note>) {
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
        _folderEntities.value = newFolders
    }
}