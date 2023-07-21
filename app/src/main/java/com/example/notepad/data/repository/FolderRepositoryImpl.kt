package com.example.notepad.data.repository

import com.example.notepad.data.db.FolderDao
import com.example.notepad.domain.models.Folder
import com.example.notepad.domain.models.mapToDomain
import com.example.notepad.domain.models.mapToFolderEntity
import com.example.notepad.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val folderDao: FolderDao
): FolderRepository {

    override suspend fun insert(folder: Folder) {
        folderDao.insert(folder.mapToFolderEntity())
    }

    override suspend fun deleteFolder(uid: String) {
        folderDao.deleteFolder(uid)
    }

    override fun getFolders(): Flow<List<Folder>> = folderDao.getFolders().map { folderEntityList ->
        folderEntityList.map { it.mapToDomain() }
    }

    override suspend fun selectedFolder(uid: String, isSelected: Boolean) {
        folderDao.selectFolder(uid, isSelected)
    }

    override suspend fun insertAll(folders: List<Folder>) {
        folderDao.insertAll(folders.map { it.mapToFolderEntity() })
    }

    override suspend fun folderList(): List<Folder> = folderDao.folderList().map { it.mapToDomain() }
}