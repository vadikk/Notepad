package com.example.notepad.data.di

import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.data.repository.FolderRepositoryImpl
import com.example.notepad.domain.repository.NoteRepository
import com.example.notepad.data.repository.NoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun createNoteRepository(noteRepositoryImpl: NoteRepositoryImpl): NoteRepository

    @Binds
    fun createFolderRepository(folderRepositoryImpl: FolderRepositoryImpl): FolderRepository
}