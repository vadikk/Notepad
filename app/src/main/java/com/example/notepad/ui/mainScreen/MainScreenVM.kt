package com.example.notepad.ui.mainScreen

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.notepad.data.di.IoDispatcher
import com.example.notepad.data.model.Folder
import com.example.notepad.data.model.Note
import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.domain.repository.NoteRepository
import com.example.notepad.ui.presentation.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainScreenVM @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): BaseVM<MainScreenState, MainScreenEvent, MainScreenEffect>() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())

    var spanCount = 1

    private var originalNotes = emptyList<Note>()
    private var selectedNotes = mutableSetOf<NoteModifyState>()

    fun init(typeScreen: TypeScreen) {
        viewModelScope.launch { noteRepository.getNotes().collect{ _notes.value = it } }

        if (typeScreen == TypeScreen.MAIN)
            viewModelScope.launch { folderRepository.getFolders().collect { _folders.value = it } }

        _folders.combine(_notes){ folders, notes ->
            if (folders.isEmpty() || folders.none { it.isSelected }) {
                setState { this.copy(folderName = "All notes") }
                fillNoteStateList(notes)
            } else {
                val selectedFolder = folders.firstOrNull { it.isSelected }
                selectedFolder?.let { folder ->
                    setState { this.copy(folderName = folder.title) }
                    val filterNotes = notes.filter { it.folder == folder.uid.toString() }
                    fillNoteStateList(filterNotes)
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun createInitialState(): MainScreenState = MainScreenState()

    override fun handleEvents(event: MainScreenEvent) {
        when(event) {
            is MainScreenEvent.SearchNote -> searchContent(event.text)
            is MainScreenEvent.SelectNote -> selectNote(event.uid, event.isChecked)
            is MainScreenEvent.SelectAll -> selectAll(event.selectState)
            is MainScreenEvent.DeleteNotes -> deleteNotes()
            is MainScreenEvent.PinNotes -> pinNotes()
            is MainScreenEvent.ClearSelectNotes -> clearSelectNotes()
        }
    }

    private fun searchContent(text: String) {
        viewModelScope.launch {
            filterNotes(text)
        }
    }

    private fun deleteNotes() {
        val pinNotes = mutableSetOf<NoteModifyState>().apply { addAll(selectedNotes) }

        viewModelScope.launch {
            pinNotes.forEach{
                noteRepository.deleteNote(it.uid.toString())
            }
        }
        setEffect { MainScreenEffect.CancelEdit }
    }

    private fun pinNotes() {
        val pinNotes = mutableSetOf<NoteModifyState>().apply { addAll(selectedNotes) }

        viewModelScope.launch {
            pinNotes.forEach{
                noteRepository.pinNote(it.uid.toString(), !it.note.isPinned)
            }
        }
        setEffect { MainScreenEffect.CancelEdit }
    }

    private fun clearSelectNotes() {
        selectedNotes.clear()
        setState { this.copy(selectNoteCount = SelectNoteCount(selectedNotes.size, false)) }
    }

    private fun selectNote(uid: Int?, isChecked: Boolean) {
        if (uid == null) return

        val selectState = if (isChecked) NoteSelectState.SELECT else NoteSelectState.NOT_SELECT
        val filterNotesByUid = currentState.modifyNotes.firstOrNull { it.uid == uid } ?: return
        val refreshNoteByUid = filterNotesByUid.copy(selectState = selectState)

        if (!selectedNotes.contains(filterNotesByUid)) selectedNotes.add(refreshNoteByUid)
        else selectedNotes.remove(filterNotesByUid)

        setState {
            this.copy(
                modifyNotes = this.modifyNotes.map {
                    if (it == filterNotesByUid) refreshNoteByUid else it
                },
                selectNoteCount = SelectNoteCount(
                    selectedNotes.size,
                    selectedNotes.size == originalNotes.size
                )
            )
        }
    }

    private fun selectAll(selectState: NoteSelectState) {
        val newNoteModifyState = originalNotes.map { note ->
            NoteModifyState(uid = note.uid, note = note, selectState = selectState)
        }

        if (selectState == NoteSelectState.NOT_SELECT) clearSelectNotes()
        else {
            selectedNotes.addAll(newNoteModifyState)
            setState {
                this.copy(selectNoteCount = SelectNoteCount(
                    selectedNotes.size,
                    selectedNotes.size == originalNotes.size
                ))
            }
        }
        setState { this.copy(modifyNotes = newNoteModifyState) }
    }

    private fun fillNoteStateList(notes: List<Note>) {
        setState {
            this.copy(modifyNotes = notes.map { note ->
                NoteModifyState(uid = note.uid, note = note, selectState = NoteSelectState.IDLE)
            })
        }
        originalNotes = notes
    }

    private suspend fun filterNotes(text: String) {
        if (text.isEmpty()) {
            setState {
                this.copy(modifyNotes = originalNotes.map { note ->
                    NoteModifyState(uid = note.uid, note = note, selectState = NoteSelectState.IDLE)
                })
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

        setState {
            this.copy(modifyNotes = filterList.map { note ->
                NoteModifyState(uid = note.uid, note = note, selectState = NoteSelectState.IDLE)
            })
        }
    }

}