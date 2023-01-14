package com.example.notepad.ui.folderList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notepad.data.di.IoDispatcher
import com.example.notepad.data.model.Folder
import com.example.notepad.data.model.Note
import com.example.notepad.data.repository.FolderRepository
import com.example.notepad.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FolderListVM @Inject constructor(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository
): ViewModel() {

    private val _folderEntities = MutableStateFlow<List<FolderEntity>>(emptyList())
    val folderEntities = _folderEntities.asStateFlow()

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

    fun selectFolder(uid: Int?, result: () -> Unit) {
        val newFolderList = folderList.map {
            if (it.uid == uid) it.copy(isSelected = true) else it.copy(isSelected = false)
        }

        viewModelScope.launch {
            folderRepository.insertAll(newFolderList)
        }
        result()
    }

    private fun fillFolderList(folders: List<Folder>, notes:List<Note>) {
        val isSelectAllNotes = folders.none { it.isSelected }

        val newFolders = mutableListOf<FolderEntity>().apply {
            add(FolderEntity.FolderIdle)
            add(FolderEntity.FolderItem(
                Folder(
                    uid = -1,
                    title = "All notes",
                    isSelected = isSelectAllNotes,
                    countNote = notes.size
                )
            ))
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
        _folderEntities.value = newFolders
    }
}

sealed class FolderEntity {
    class FolderItem(val folder: Folder) : FolderEntity()
    object FolderIdle : FolderEntity()
}