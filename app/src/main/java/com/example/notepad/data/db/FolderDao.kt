package com.example.notepad.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notepad.data.model.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: Folder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(folder: List<Folder>)

    @Query("DELETE FROM folder WHERE uid LIKE :uid")
    suspend fun deleteFolder(uid: String)

    @Query("SELECT * FROM folder")
    fun getFolders(): Flow<List<Folder>>

    @Query("UPDATE folder SET isSelected =:isSelected WHERE uid LIKE :uid")
    suspend fun selectFolder(uid: String, isSelected: Boolean)

    @Query("SELECT * FROM folder")
    suspend fun folderList(): List<Folder>
}