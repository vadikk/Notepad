package com.example.notepad.domain.models

import com.example.notepad.R
import com.example.notepad.data.db.models.NoteEntity

data class Note(
    var uid: Int? = null,
    val date: String = "",
    val folder: String = "",
    val title: String = "",
    val description: String = "",
    val isPinned: Boolean = false,
    val bgColor: Int = R.color.white,
    val password: String = ""
)

fun NoteEntity.mapToDomain(): Note =
    Note(uid, date, folder, title, description, isPinned, bgColor, password)

fun Note.mapToNoteEntity(): NoteEntity =
    NoteEntity(uid, date, folder, title, description, isPinned, bgColor, password)