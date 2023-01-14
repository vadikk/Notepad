package com.example.notepad.ui.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notepad.data.di.IoDispatcher
import com.example.notepad.data.model.Folder
import com.example.notepad.data.model.Note
import com.example.notepad.data.repository.FolderRepository
import com.example.notepad.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainScreenVM @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    private val _notesState = MutableStateFlow<List<NoteModifyState>>(emptyList())
    val notesState = _notesState.asStateFlow()
    private val _selectCount = MutableStateFlow(SelectNoteCount(0, false))
    val selectCount = _selectCount.asStateFlow()
    private val _folderName = MutableStateFlow("")
    val folderName = _folderName.asStateFlow()

    var spanCount = 1

    private var originalNotes = emptyList<Note>()
    private var selectedNotes = mutableSetOf<NoteModifyState>()

    fun init(typeScreen: TypeScreen) {
        viewModelScope.launch { noteRepository.getNotes().collect{ _notes.value = it } }

        if (typeScreen == TypeScreen.MAIN)
            viewModelScope.launch { folderRepository.getFolders().collect { _folders.value = it } }

        _folders.combine(_notes){ folders, notes ->
            if (folders.isEmpty() || folders.none { it.isSelected }) {
                _folderName.value = "All notes"
                fillNoteStateList(notes)
            } else {
                val selectedFolder = folders.firstOrNull { it.isSelected }
                selectedFolder?.let { folder ->
                    _folderName.value = folder.title
                    val filterNotes = notes.filter { it.folder == folder.uid.toString() }
                    fillNoteStateList(filterNotes)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun searchContent(text: String) {
        viewModelScope.launch {
            filterNoteList(text)
        }
    }

    fun deleteNotes(callBack: () -> Unit) {
        val pinNotes = mutableSetOf<NoteModifyState>().apply { addAll(selectedNotes) }

        viewModelScope.launch {
            pinNotes.forEach{
                noteRepository.deleteNote(it.uid.toString())
            }
        }
        callBack()
    }

    fun pinNotes(callBack: () -> Unit) {
        val pinNotes = mutableSetOf<NoteModifyState>().apply { addAll(selectedNotes) }

        viewModelScope.launch {
            pinNotes.forEach{
                noteRepository.pinNote(it.uid.toString(), !it.note.isPinned)
            }
        }
        callBack()
    }

    fun clearSelectNotes() {
        selectedNotes.clear()
        _selectCount.value = SelectNoteCount(selectedNotes.size, false)
    }

    fun selectNote(uid: Int?, isChecked: Boolean) {
        if (uid == null) return

        val selectState = if (isChecked) NoteSelectState.SELECT else NoteSelectState.NOT_SELECT
        val filterNotesByUid = _notesState.value.filter { it.uid == uid }.firstOrNull() ?: return
        val refreshNoteByUid = filterNotesByUid.copy(selectState = selectState)

        if (!selectedNotes.contains(filterNotesByUid)) selectedNotes.add(refreshNoteByUid)
        else selectedNotes.remove(filterNotesByUid)

        _selectCount.value = SelectNoteCount(
            selectedNotes.size,
            selectedNotes.size == originalNotes.size
        )
        _notesState.value = _notesState.value.map {
            if (it == filterNotesByUid) refreshNoteByUid else it
        }
    }

    fun selectAll(selectState: NoteSelectState) {
        val newNoteModifyState = originalNotes.map { note ->
            NoteModifyState(uid = note.uid, note = note, selectState = selectState)
        }

        if (selectState == NoteSelectState.NOT_SELECT) clearSelectNotes()
        else {
            selectedNotes.addAll(newNoteModifyState)
            _selectCount.value = SelectNoteCount(
                selectedNotes.size,
                selectedNotes.size == originalNotes.size
            )
        }

        _notesState.value = newNoteModifyState
    }

    private fun fillNoteStateList(notes: List<Note>) {
        _notesState.value = notes.map { note ->
            NoteModifyState(uid = note.uid, note = note, selectState = NoteSelectState.IDLE)
        }
        originalNotes = notes
    }

    private suspend fun filterNoteList(text: String) {
        if (text.isEmpty()) {
            _notesState.value = originalNotes.map { note ->
                NoteModifyState(uid = note.uid, note = note, selectState = NoteSelectState.IDLE)
            }
            return
        }

        val filterList = mutableListOf<Note>()

        withContext(dispatcher){
            originalNotes.forEach { note ->
                if (note.title.lowercase().contains(text.lowercase()) ||
                        note.description.lowercase().contains(text.lowercase()))
                    filterList.add(note)
            }
        }

        _notesState.value = filterList.map { note ->
            NoteModifyState(uid = note.uid, note = note, selectState = NoteSelectState.IDLE)
        }
    }

}

data class NoteModifyState(
    val uid: Int?,
    val note: Note,
    val selectState: NoteSelectState
)

data class SelectNoteCount(
    val selectCount: Int,
    val isSelectAll: Boolean
)

enum class NoteSelectState{
    IDLE, SELECT, NOT_SELECT
}

enum class TypeScreen{
    MAIN, GROUP
}