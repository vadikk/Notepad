package com.example.notepad.data.repository

import com.example.notepad.data.db.NoteDao
import com.example.notepad.data.model.Note
import com.example.notepad.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
): NoteRepository {

    override suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    override suspend fun getNoteById(uid: String): Note? = noteDao.getNoteById(uid)

    override fun getNotes(): Flow<List<Note>> = noteDao.getNotes()

    override suspend fun deleteNote(uid: String) {
        noteDao.deleteNote(uid)
    }

    override suspend fun pinNote(uid: String, isPinned: Boolean) {
        noteDao.pinNote(uid, isPinned)
    }

    override suspend fun notes(): List<Note> = noteDao.notes()
}