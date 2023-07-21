package com.example.notepad.ui.folderList

import com.example.notepad.domain.models.Folder
import com.example.notepad.ui.presentation.Effect
import com.example.notepad.ui.presentation.Event
import com.example.notepad.ui.presentation.State

data class FoldersState(
    val folders: List<FolderEntityContract> = emptyList()
): State

sealed class FolderEntityContract {
    class FolderItem(val folder: Folder) : FolderEntityContract()
    object FolderIdle : FolderEntityContract()
}

sealed class FoldersEffect: Effect{
    object CloseScreen: FoldersEffect()
}

sealed class FoldersEvent: Event{
    data class SelectFolder(val uid: Int?): FoldersEvent()
}