package com.example.notepad.ui.createNote

import com.example.notepad.R
import com.example.notepad.ui.presentation.Effect
import com.example.notepad.ui.presentation.Event
import com.example.notepad.ui.presentation.State

data class CreateNoteState(
    val title: String = "",
    val description: String = "",
    val color: Int = R.color.white,
    val date: String = "",
    val password: String = "",
    val isPinned: Boolean = false,
    val folder: String = "",
    val isApplyBtnEnabled: Boolean = false
): State

sealed class NoteEvent: Event {
    data class ChangeTitle(val title: String): NoteEvent()
    data class ChangeDescription(val description: String): NoteEvent()
    data class ChangeColor(val color: Int): NoteEvent()
    data class ChangePassword(val password: String): NoteEvent()
    data class Folder(val uid: String): NoteEvent()
    object Save: NoteEvent()
    object Delete: NoteEvent()
}

sealed class NoteEffect: Effect{
    object CloseScreen: NoteEffect()
}