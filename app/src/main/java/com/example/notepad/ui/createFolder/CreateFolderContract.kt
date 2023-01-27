package com.example.notepad.ui.createFolder

import com.example.notepad.ui.presentation.Effect
import com.example.notepad.ui.presentation.State

sealed class CreateFolderEffect: Effect{
    object CloseScreen: CreateFolderEffect()
}

sealed class CreateFolderState: State{
    object Idle: CreateFolderState()
}