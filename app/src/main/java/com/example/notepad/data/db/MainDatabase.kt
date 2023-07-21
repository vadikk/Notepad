package com.example.notepad.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notepad.data.db.models.FolderEntity
import com.example.notepad.data.db.models.NoteEntity

@Database(entities = [NoteEntity::class, FolderEntity::class], version = 1)
abstract class MainDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao
}