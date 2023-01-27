package com.example.notepad.ui.mainScreen

import com.example.notepad.data.model.Note
import com.example.notepad.ui.presentation.Effect
import com.example.notepad.ui.presentation.Event
import com.example.notepad.ui.presentation.State

data class MainScreenState(
    val modifyNotes: List<NoteModifyState> = emptyList(),
    val selectNoteCount: SelectNoteCount? = null,
    val folderName: String = ""
): State

sealed class MainScreenEvent: Event{
    data class SearchNote(val text: String): MainScreenEvent()
    data class SelectNote(val uid: Int?, val isChecked: Boolean): MainScreenEvent()
    data class SelectAll(val selectState: NoteSelectState): MainScreenEvent()
    object DeleteNotes: MainScreenEvent()
    object PinNotes: MainScreenEvent()
    object ClearSelectNotes: MainScreenEvent()
}

sealed class MainScreenEffect: Effect{
    object CancelEdit: MainScreenEffect()
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