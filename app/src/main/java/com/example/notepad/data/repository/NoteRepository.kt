package com.example.notepad.data.repository

import com.example.notepad.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun insert(note: Note)
    suspend fun getNoteById(uid: String): Note?
    fun getNotes(): Flow<List<Note>>
    suspend fun deleteNote(uid: String)
    suspend fun pinNote(uid: String, isPinned: Boolean)
    suspend fun notes(): List<Note>
}