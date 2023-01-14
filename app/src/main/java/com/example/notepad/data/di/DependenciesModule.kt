package com.example.notepad.data.di

import com.example.notepad.domain.usecase.ValidateNoteField
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class DependenciesModule {

    @Provides
    @ViewModelScoped
    fun getValidateInputField(): ValidateNoteField = ValidateNoteField()
}