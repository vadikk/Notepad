package com.example.notepad.data.repository

import com.example.notepad.data.db.NoteDao
import com.example.notepad.domain.models.Note
import com.example.notepad.domain.models.mapToDomain
import com.example.notepad.domain.models.mapToNoteEntity
import com.example.notepad.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun insert(note: Note) {
        noteDao.insert(note.mapToNoteEntity())
    }

    override suspend fun getNoteById(uid: String): Note? = noteDao.getNoteById(uid)?.mapToDomain()

    override fun getNotes(): Flow<List<Note>> = noteDao.getNotes().map { noteEntityList ->
        noteEntityList.map { it.mapToDomain() }
    }

    override suspend fun deleteNote(uid: String) {
        noteDao.deleteNote(uid)
    }

    override suspend fun pinNote(uid: String, isPinned: Boolean) {
        noteDao.pinNote(uid, isPinned)
    }

    override suspend fun notes(): List<Note> = noteDao.notes().map { it.mapToDomain() }
}