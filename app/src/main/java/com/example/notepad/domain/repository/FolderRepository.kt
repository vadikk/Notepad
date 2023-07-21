package com.example.notepad.domain.repository

import com.example.notepad.domain.models.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    suspend fun insert(folder: Folder)
    suspend fun deleteFolder(uid: String)
    fun getFolders(): Flow<List<Folder>>
    suspend fun selectedFolder(uid: String, isSelected: Boolean)
    suspend fun insertAll(folders: List<Folder>)
    suspend fun folderList(): List<Folder>
}