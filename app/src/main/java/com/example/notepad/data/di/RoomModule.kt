package com.example.notepad.data.di

import android.app.Application
import androidx.room.Room
import com.example.notepad.data.db.FolderDao
import com.example.notepad.data.db.MainDatabase
import com.example.notepad.data.db.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun getDBInstance(application: Application): MainDatabase {
        val roomBuilder = Room
            .databaseBuilder(application, MainDatabase::class.java, "main_database")
            .fallbackToDestructiveMigration()
        return roomBuilder.build()
    }

    @Singleton
    @Provides
    fun getNoteDao(dbInstance: MainDatabase): NoteDao = dbInstance.noteDao()

    @Singleton
    @Provides
    fun getFolderDao(dbInstance: MainDatabase): FolderDao = dbInstance.folderDao()
}