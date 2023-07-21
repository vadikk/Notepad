package com.example.notepad.ui.createNote

import androidx.annotation.StringRes
import com.example.notepad.R

sealed class NoteMenuAction(@StringRes val title: Int)
data class AddToFolder(@StringRes val titleRes: Int): NoteMenuAction(titleRes)
data class Delete(@StringRes val titleRes: Int): NoteMenuAction(titleRes)
data class ChangeBgColor(@StringRes val titleRes: Int): NoteMenuAction(titleRes)
object SetDeletePassword: NoteMenuAction(-1)

val noteMenuActions = listOf(
    AddToFolder(R.string.add_to_folder),
    Delete(R.string.delete),
    ChangeBgColor(R.string.change_bg_color),
    SetDeletePassword,
)