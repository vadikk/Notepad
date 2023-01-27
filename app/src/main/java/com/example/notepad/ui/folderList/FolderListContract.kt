package com.example.notepad.ui.folderList

import com.example.notepad.data.model.Folder
import com.example.notepad.ui.presentation.Effect
import com.example.notepad.ui.presentation.Event
import com.example.notepad.ui.presentation.State

data class FoldersState(
    val folderEntities: List<FolderEntity> = emptyList()
): State

sealed class FolderEntity {
    class FolderItem(val folder: Folder) : FolderEntity()
    object FolderIdle : FolderEntity()
}

sealed class FoldersEffect: Effect{
    object CloseScreen: FoldersEffect()
}

sealed class FoldersEvent: Event{
    data class SelectFolder(val uid: Int?): FoldersEvent()
}