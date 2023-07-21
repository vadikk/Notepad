package com.example.notepad.ui.folderList

import com.example.notepad.ui.presentation.State

data class FolderListState(
    val folders: List<FolderEntityContract> = emptyList()
): State