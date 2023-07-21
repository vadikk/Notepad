package com.example.notepad.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notepad.data.db.models.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteEntity: NoteEntity)

    @Query("DELETE FROM note WHERE uid LIKE :uid")
    suspend fun deleteNote(uid: String)

    @Query("SELECT * FROM note WHERE uid LIKE :uid")
    suspend fun getNoteById(uid: String): NoteEntity?

    @Query("SELECT * FROM note ORDER BY isPinned IS 0, date DESC")
    fun getNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note")
    suspend fun notes(): List<NoteEntity>

    @Query("UPDATE note SET isPinned =:isPinned WHERE uid LIKE :uid")
    suspend fun pinNote(uid: String, isPinned: Boolean)
}