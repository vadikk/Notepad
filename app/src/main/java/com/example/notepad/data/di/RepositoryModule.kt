package com.example.notepad.data.di

import com.example.notepad.domain.repository.FolderRepository
import com.example.notepad.data.repository.FolderRepositoryImpl
import com.example.notepad.domain.repository.NoteRepository
import com.example.notepad.data.repository.NoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    @ViewModelScoped
    fun createNoteRepository(noteRepositoryImpl: NoteRepositoryImpl): NoteRepository

    @Binds
    @ViewModelScoped
    fun createFolderRepository(folderRepositoryImpl: FolderRepositoryImpl): FolderRepository
}