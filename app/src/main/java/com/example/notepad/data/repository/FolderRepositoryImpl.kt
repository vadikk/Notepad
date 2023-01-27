package com.example.notepad.data.repository

import com.example.notepad.data.db.FolderDao
import com.example.notepad.data.model.Folder
import com.example.notepad.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val folderDao: FolderDao
): FolderRepository {

    override suspend fun insert(folder: Folder) {
        folderDao.insert(folder)
    }

    override suspend fun deleteFolder(uid: String) {
        folderDao.deleteFolder(uid)
    }

    override fun getFolders(): Flow<List<Folder>> = folderDao.getFolders()

    override suspend fun selectedFolder(uid: String, isSelected: Boolean) {
        folderDao.selectFolder(uid, isSelected)
    }

    override suspend fun insertAll(folders: List<Folder>) {
        folderDao.insertAll(folders)
    }

    override suspend fun folderList(): List<Folder> = folderDao.folderList()
}