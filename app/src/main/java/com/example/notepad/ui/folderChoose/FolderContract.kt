package com.example.notepad.ui.folderChoose

import com.example.notepad.ui.folderList.FolderEntity
import com.example.notepad.ui.presentation.State

data class FolderListState(
    val folderEntities: List<FolderEntity> = emptyList()
): State